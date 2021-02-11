package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class Index {
  private SearchManager searchManager;
  private SettingsManager settingsManager;
  private UserAccessor userAccessor;

  private final FedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();


  @Autowired
  public Index(FedexIdeaService fedexIdeaService, @ComponentImport SearchManager searchManager, @ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor) {
    this.searchManager = searchManager;
    this.settingsManager = settingsManager;
    this.fedexIdeaService = fedexIdeaService;
    this.userAccessor = userAccessor;
  }

  /**
   * Clean the of non ASCII and control characters
   *
   * @param text input string
   * @return cleaned string
   */
  private static String cleanTextContent(String text) {
    return text
      .replaceAll("[^\\x00-\\x7F]", "")
      .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
      .replaceAll("\\p{C}", "");
  }

  /**
   * @param searchString to find technologies that begin with this string
   * @param response     Servlet contest
   * @return String in the form of a json list of TechnologyAPI objects
   */
  @Path("/technology")
  @Produces({"application/json"})
  @GET
  public String getTechList(
    @QueryParam("q") String searchString,
    @Context HttpServletResponse response
  ) {
    String normalizeSearch = searchString != null && searchString.length() > 0
      ? cleanTextContent(searchString).toLowerCase()
      : null;
    this.applyNoCacheHeaders(response);

    boolean searchKeyHasValue = normalizeSearch != null && !normalizeSearch.trim().isEmpty();

    List<TechnologyAPI> allTechnologies = searchKeyHasValue
      ? this.fedexIdeaService.queryTechList(normalizeSearch)
      : this.fedexIdeaService.queryTechList();

    if (searchKeyHasValue && allTechnologies.isEmpty() && normalizeSearch.endsWith(",")) {
      TechnologyAPI newTech = new TechnologyAPI(
        normalizeSearch.replace(",", "")
      );
      allTechnologies.add(0, newTech);
    }

    return allTechnologies.isEmpty()
      ? "{[]}"
      : this.gson.toJson(allTechnologies);
  }

  /**
   * Gets latest schema
   *
   * @param response Servlet response to populate
   * @return JSON string representation of latest schema
   */
  @Path("/schema")
  @Produces({"application/json"})
  @GET
  public String getSchema( @Context HttpServletResponse response){
    List<FedexSchema> allSchema = this.fedexIdeaService.listSchemas();
    FedexSchema latestSchema = allSchema.isEmpty() ? (new FedexSchema.Builder()).build() : allSchema.get(allSchema.size() - 1);

    return this.gson.toJson(latestSchema);
  }

  /**
   * Gets brief information about the schemas 
   * (id, name, version and description)
   *
   * @param response Servlet response to populate
   * @return JSON string representation of brief schema information
   */
  @Path("/schema/ids")
  @Produces({"application/json"})
  @GET
  public String getSchemaIds( @Context HttpServletResponse response){
    List<Map> schemaIds = this.fedexIdeaService.listSchemas().stream().map(schema -> {
      Map schemaReturn = new HashMap<String, String>();
      schemaReturn.put("id",  schema.getGlobalId());
      schemaReturn.put("name",  schema.getName());
      schemaReturn.put("version",  schema.getVersion());
      schemaReturn.put("description",  schema.getDescription());
      return  schemaReturn;
    }).collect(Collectors.toList());

    return this.gson.toJson( schemaIds);
  }

  /**
   * Find and return idea pages based on query
   *
   * @param title the query on title field
   * @param description the query on description field
   * @param status the query on status
   * @param owner the query on owner
   * @param response the servlet response to populate
   * @return A json string containing all found idea pages
   */
  @Path("/ideapages")
  @Produces({"application/json"})
  @GET
  public String getIdeaPages(
    @QueryParam("title") String title,
    @QueryParam("description") String description,
    @QueryParam("status") String status,
    @QueryParam("owner") String owner,
    @Context HttpServletResponse response
  ) {
    this.applyNoCacheHeaders(response);

    Set<String> newSet = new HashSet<>();
    newSet.add("fedex-ideas");

    title = title != null ? title : "";
    description = description != null ? description : "";
    status = status != null ? status : "";
    owner = owner != null ? owner : "";

    List<FedexIdea> allIdeas = this.fedexIdeaService.queryAllFedexIdea(title, description, status, owner);

    List<Map> preConvert = allIdeas.stream().map( idea -> {
      Map preJsonIdea = new HashMap<String, String>();
      preJsonIdea.put("title", idea.getTitle());
      preJsonIdea.put("url", idea.getUrl());
      preJsonIdea.put("description", idea.getDescription().isEmpty() ? "":idea.getDescription());
      preJsonIdea.put("technologies", idea.getTechnologies().isEmpty() ? "" : idea.getTechnologies().stream().map(tech-> tech.getTechnology()).collect(
        Collectors.toList()));

      preJsonIdea.put("owner", idea.getOwner());

      preJsonIdea.put("status", idea.getStatus());

      return preJsonIdea;
    }).collect(Collectors.toList());

    return this.gson.toJson(preConvert);
  }

  // extracts body from request
  private static String extractPostRequestBody(HttpServletRequest request) throws IOException {
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      try {
        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } catch (IOException e){
        throw new IOException("Failed to parse request body.", e);
      }
    }
    return "";
  }

  /**
   * Posts a complete schema (schema, uiSchema and indexSchema)
   *
   * @param request Servlet request to extract post body from
   * @param response Servlet response to populate
   * @return JSON string representation of the updated schema
   */
  @Path("/schema")
  @Consumes({"application/json"})
  @POST
  public String postSchema(
    @Context HttpServletRequest request,
    @Context HttpServletResponse response
  ) throws IOException {
    this.applyNoCacheHeaders(response);
    String schemaBody = "";

    schemaBody = extractPostRequestBody(request);

    FedexSchema mappedSchemaBody = this.gson.fromJson(schemaBody, FedexSchema.class);

    List<FedexSchema> allSchema = this.fedexIdeaService.listSchemas();

    FedexSchema latestSchema = allSchema.isEmpty() ? (new FedexSchema.Builder()).build() : allSchema.get(allSchema.size() - 1);
    
    SchemaMapper.MAPPER.mapToSchema(mappedSchemaBody, latestSchema);

    FedexSchema createdSchema = this.fedexIdeaService.createSchema(latestSchema);

    return this.gson.toJson(createdSchema);
  }

  /**
   * Added to prevent the search caching the responses
   *
   * @param response Servlet response to populate
   */
  private void applyNoCacheHeaders(HttpServletResponse response) {
    CachingHeaders.PREVENT_CACHING.apply(response);
  }
}
