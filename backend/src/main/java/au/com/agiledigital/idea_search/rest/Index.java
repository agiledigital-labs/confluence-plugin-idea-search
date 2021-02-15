package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

  private final FedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();


  @Autowired
  public Index(FedexIdeaService fedexIdeaService) {
    this.fedexIdeaService = fedexIdeaService;
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

    List<FedexIdea> allIdeas = this.fedexIdeaService.queryAllFedexIdea();

    List<Map> preConvert = allIdeas.stream().map( idea -> {
      Map preJsonIdea = new HashMap<String, String>();
      preJsonIdea.put("title", idea.getTitle());
      preJsonIdea.put("url", idea.getUrl());

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
