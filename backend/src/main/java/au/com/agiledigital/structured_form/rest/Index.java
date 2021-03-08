package au.com.agiledigital.structured_form.rest;

import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormIndex;
import au.com.agiledigital.structured_form.model.FormIndexQuery;
import au.com.agiledigital.structured_form.model.FormSchema;
import au.com.agiledigital.structured_form.service.FormDataService;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class Index {

  private final FormDataService formDataService;
  private final Gson gson = new Gson();
  @ComponentImport
  private final SettingsService settingsService;
  private final PageService pageService;

  @Autowired
  public Index(FormDataService formDataService,
               @Qualifier("settingsService")
               @ComponentImport SettingsService settingsService,
               @ComponentImport PageService pageService) {
    this.formDataService = formDataService;
    this.settingsService = settingsService;
    this.pageService = pageService;
  }

  // extracts body from request
  @Nonnull
  private static String extractPostRequestBody(@Nonnull HttpServletRequest request) throws IOException {
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
   * Gets latest schema
   *
   * @param response Servlet response to populate
   * @return JSON string representation of latest schema
   */
  @Path("/schema")
  @Produces({"application/json"})
  @GET
  public String getSchema(@Context HttpServletResponse response) {
    return this.gson.toJson(this.formDataService.getCurrentSchema());
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
    List<?> schemaIds = this.formDataService.listSchemas().stream().map(schema -> {
      Map<String, Object> schemaReturn = new HashMap<>();
      schemaReturn.put("id", schema.getGlobalId());
      schemaReturn.put("name", schema.getName());
      schemaReturn.put("version", schema.getVersion());
      schemaReturn.put("description", schema.getDescription());
      return schemaReturn;
    }).collect(Collectors.toList());

    return this.gson.toJson(schemaIds);
  }

  /**
   * Find and return form data pages based on query
   *
   * @param response the servlet response to populate
   * @return A json string containing all found form data pages
   */
  @Path("/form-data")
  @Produces({"application/json"})
  @GET
  public String getFormDataPages(
    @Nonnull @Context HttpServletRequest request,
    @Context HttpServletResponse response
  ) throws UnsupportedEncodingException {
    this.applyNoCacheHeaders(response);

    // Creates a new FormIndexQuery when there is content after the =. queries like `?string1=test` will be created
    // where queries like ?number0=` will not.
    List<FormIndexQuery> queries = Arrays.stream(StringUtils.split(URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8.toString()), "&"))
      .map(queryStrings -> StringUtils.split(queryStrings, "="))
      .filter(queryStrings -> queryStrings.length > 1)
      .map(queryStrings ->
        new FormIndexQuery(queryStrings[0], queryStrings[1])
      )
      .collect(Collectors.toList());

    List<FormData> allFormData = queries.isEmpty() ? this.formDataService.queryAllFormData() : this.formDataService.queryAllFormData(queries);

    List<?> preConvert = allFormData.stream().map(formData -> {
      Map<String, Object> preJsonFormData = new HashMap<>();
      preJsonFormData.put("title", formData.getTitle());
      preJsonFormData.put("url", getPageUrl(formData.getContentId()));
      preJsonFormData.put("creator", formData.getCreator());
      preJsonFormData.put("indexData", formData.getIndexData().stream().map(FormIndex::getAsMap).filter(r -> !r.isEmpty() && r != null).toArray());

      return preJsonFormData;
    }).collect(Collectors.toList());

    return this.gson.toJson(preConvert);
  }

  @Nonnull
  private String getPageUrl(@Nonnull ContentId contentId) {
    try {
      return this.settingsService.getGlobalSettings().getBaseUrl() +
        this.pageService.getIdPageLocator(contentId.asLong()).getPage().getUrlPath();
    } catch (NullPointerException nullPointerException) {
      return this.settingsService.getGlobalSettings().getBaseUrl();
    }
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
    @Nonnull @Context HttpServletRequest request,
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
