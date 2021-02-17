package au.com.agiledigital.structured_form.rest;

import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormSchema;
import au.com.agiledigital.structured_form.service.FormDataService;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class Index {

  private final FormDataService formDataService;
  private Gson gson = new Gson();
  private SettingsManager settingsManager;
  private PageService pageService;

  @Autowired
  public Index(FormDataService formDataService,
               SettingsManager settingsManager,
               PageService pageService) {
    this.formDataService = formDataService;
    this.settingsManager = settingsManager;
    this.pageService = pageService;
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
  public String getSchema(@Context HttpServletResponse response) {
    List<FormSchema> allSchema = this.formDataService.listSchemas();
    FormSchema latestSchema = allSchema.isEmpty() ? (new FormSchema.Builder()).build() : allSchema.get(allSchema.size() - 1);

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
  public String getSchemaIds(@Context HttpServletResponse response) {
    List<Map> schemaIds = this.formDataService.listSchemas().stream().map(schema -> {
      Map schemaReturn = new HashMap<String, String>();
      schemaReturn.put("id", schema.getGlobalId());
      schemaReturn.put("name", schema.getName());
      schemaReturn.put("version", schema.getVersion());
      schemaReturn.put("description", schema.getDescription());
      return schemaReturn;
    }).collect(Collectors.toList());

    return this.gson.toJson(schemaIds);
  }

  /**
   * Find and return idea pages based on query
   *
   * @param response    the servlet response to populate
   * @return A json string containing all found idea pages
   */
  @Path("/ideapages")
  @Produces({"application/json"})
  @GET
  public String getIdeaPages(
    @Context HttpServletRequest request,
    @Context HttpServletResponse response
  ) {
    this.applyNoCacheHeaders(response);

   List<AbstractMap.SimpleEntry> test = Arrays.stream(StringUtils.split(request.getQueryString(), "&")).map(string -> StringUtils.split(string, "=")).map(r -> new AbstractMap.SimpleEntry(r[0], r[1])).collect(Collectors.toList());


    List<FormData> allIdeas = this.formDataService.queryAllFedexIdea();
//    .stream().filter(idea -> idea.getIndexData().stream().anyMatch(r -> test.contains(r))).collect(Collectors.toList());
    String indexSchema = this.formDataService.getCurrentSchema().getIndexSchema();

    List<Map> preConvert = allIdeas.stream().map(idea -> {
      Map preJsonIdea = new HashMap<String, String>();
      preJsonIdea.put("title", idea.getTitle());
      preJsonIdea.put("url", getPageUrl(idea.getContentId()));
      preJsonIdea.put("creator", idea.getCreator().getName());
      preJsonIdea.put("indexData", idea.getIndexData());
      preJsonIdea.put("indexSchema", this.gson.fromJson(indexSchema, Map.class));

      return preJsonIdea;
    }).collect(Collectors.toList());

    return this.gson.toJson(preConvert);
  }

  @Nonnull
  private String getPageUrl(ContentId contentId) {
    try{
      return new StringBuilder()
        .append(this.settingsManager.getGlobalSettings().getBaseUrl())
        .append(this.pageService.getIdPageLocator(contentId.asLong()).getPage().getUrlPath())
        .toString();
    } catch (NullPointerException nullPointerException) {
      return this.settingsManager.getGlobalSettings().getBaseUrl();
    }
  }

  // extracts body from request
  private static String extractPostRequestBody(HttpServletRequest request) throws IOException {
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      try {
        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
      } catch (IOException e) {
        throw new IOException("Failed to parse request body.", e);
      }
    }
    return "";
  }

  /**
   * Posts a complete schema (schema, uiSchema and indexSchema)
   *
   * @param request  Servlet request to extract post body from
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

    FormSchema mappedSchemaBody = this.gson.fromJson(schemaBody, FormSchema.class);

    List<FormSchema> allSchema = this.formDataService.listSchemas();

    FormSchema latestSchema = allSchema.isEmpty() ? (new FormSchema.Builder()).build() : allSchema.get(allSchema.size() - 1);

    SchemaMapper.MAPPER.mapToSchema(mappedSchemaBody, latestSchema);

    FormSchema createdSchema = this.formDataService.createSchema(latestSchema);

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
