package au.com.agiledigital.idea_search.rest;


import au.com.agiledigital.idea_search.model.FedexIdea;

import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.web.filter.CachingHeaders;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class TechnologyList {

  private final FedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();


  @Autowired
  public TechnologyList(FedexIdeaService fedexIdeaService) {
    this.fedexIdeaService = fedexIdeaService;
  }

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
  public String getSchemaIds(@Context HttpServletResponse response) {
    List<Map> schemaIds = this.fedexIdeaService.listSchemas().stream().map(schema -> {
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


    List<FedexIdea> allIdeas = this.fedexIdeaService.queryAllFedexIdea(title, description, status, owner);

    List<Map> preConvert = allIdeas.stream().map( idea -> {
      Map preJsonIdea = new HashMap<String, String>();
      preJsonIdea.put("title", idea.getTitle());
      preJsonIdea.put("url", idea.getUrl());

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
