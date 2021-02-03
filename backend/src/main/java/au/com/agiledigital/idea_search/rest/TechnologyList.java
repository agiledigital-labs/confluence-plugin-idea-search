package au.com.agiledigital.idea_search.rest;

import static au.com.agiledigital.idea_search.helpers.Utilities.getRows;

import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import au.com.agiledigital.idea_search.model.FedexIdea;

import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.DefaultUserAccessor;
import com.atlassian.confluence.user.DefaultUserDetailsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.base.Splitter;
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
import javax.ws.rs.PUT;
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
public class TechnologyList {
  private SearchManager searchManager;
  private SettingsManager settingsManager;
  private UserAccessor userAccessor;

  private final FedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();


  @Autowired
  public TechnologyList(FedexIdeaService fedexIdeaService, @ComponentImport SearchManager searchManager, @ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor) {
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
  public static String userKey = "2c9d829d6e61f011016e61f143ff0000";

  private static String DEFAULT_SCHEMA = "{\n" +
    "  \"title\": \"A fedex Idea or puzzle\",\n" +
    "  \"description\": \"Something interesting that could be worked on either in downtime or a fedex day\",\n" +
    "  \"type\": \"object\",\n" +
    "  \"required\": [\n" +
    "    \"ideaTitle\"\n" +
    "  ],\n" +
    "  \"properties\": {\n" +
    "    \"ideaTitle\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea Title (or how it should be know)\",\n" +
    "      \"default\": \"Other things\"\n" +
    "    },\n" +
    "    \"description\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Description\"\n" +
    "    },\n" +
    "    \"owner\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea owner\"\n" +
    "    },\n" +
    "    \"status\":{\n" +
    "          \"type\": \"string\",\n" +
    "          \"enum\": [\n" +
    "            \"new\",\n" +
    "            \"inProgress\",\n" +
    "            \"completed\",\n" +
    "            \"abandoned\"\n" +
    "          ],\"enumNames\": [\"New\", \"In Progress\", \"Completed\", \"Abandoned\"],\n" +
    "          \"default\": \"New\"\n" +
    "        \n" +
    "    },\n" +
    "        \"team\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The team\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "       \"technologies\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The tech\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "           \"links\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to resources for this idea\"\n" +
    "    },\n" +
    "           \"tickets\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to issues or tickets that track this\"\n" +
    "    },\n" +
    "           \"talks\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Presentations on the idea\"\n" +
    "    }\n" +
    "  }\n" +
    "}";


  @Path("/schema")
  @Produces({"application/json"})
  @GET
  public String getSchema( @Context HttpServletResponse response){
    List<FedexSchema> allSchema = this.fedexIdeaService.listSchemas();
    FedexSchema latestSchema = allSchema.get(allSchema.size() - 1);

    return this.gson.toJson(latestSchema);
  }

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
   * @param searchString to find technologies that begin with this string
   * @param response     Servlet contest
   * @return String in the form of a json list of TechnologyAPI objects
   */
  @Path("/ideaPages")
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


    //List<IdeaContainer> allIdeas = getRows(newSet, "ds", this.searchManager, this.settingsManager);
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

    return allIdeas.isEmpty()
      ? "[{}]"
      : this.gson.toJson(preConvert);
  }

  private static String extractPostRequestBody(HttpServletRequest request) throws IOException {
    if ("PUT".equalsIgnoreCase(request.getMethod())) {
      try {
        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } catch (Exception e){
        return "Error in getting input stream";
      }
    }
    return "Nothing to see here";
  }

  /**
   * @param searchString to find technologies that begin with this string
   * @param response     Servlet contest
   * @return String in the form of a json list of TechnologyAPI objects
   */
  @Path("/schema")
  @Consumes({"application/json"})
  @PUT
  public String putSchema(
    @QueryParam("type") String type,
    @Context HttpServletRequest request,
    @Context HttpServletResponse response
  ) {
    this.applyNoCacheHeaders(response);
    String schemaBody = "";

    try {
      schemaBody = extractPostRequestBody(request);
    } catch (Exception e){
      throw new Error("Error parsing request body");
    }

    Map<String, String> mappedSchemaBody = this.gson.fromJson(schemaBody, Map.class);

    List<FedexSchema> allSchema = this.fedexIdeaService.listSchemas();
    FedexSchema latestSchema = allSchema.get(allSchema.size() - 1);

    switch (type){
      case "index-schema":
        latestSchema.setIndexSchema(mappedSchemaBody.get("data"));
        break;
      case "ui-schema":
        latestSchema.setUiSchema(mappedSchemaBody.get("data"));
        break;
      case "idea-schema":
        latestSchema.setSchema(mappedSchemaBody.get("data"));
        break;
      default:
        break;
    }

    FedexSchema createdSchema = this.fedexIdeaService.createSchema(latestSchema);

    try {
      return this.gson.toJson(createdSchema.getUiSchema());
    } catch (Exception e){
      return this.gson.toJson("There is an error");
    }
  }

  /**
   * Added to prevent the search caching the responses
   *
   * @param response
   */
  private void applyNoCacheHeaders(HttpServletResponse response) {
    CachingHeaders.PREVENT_CACHING.apply(response);
  }
}
