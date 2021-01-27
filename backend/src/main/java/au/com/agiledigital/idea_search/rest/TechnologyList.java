package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.helpers.StructureDataRenderHelper;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.filter.SubsetResultFilter;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.google.gson.Gson;
import net.java.ao.Searchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import au.com.agiledigital.idea_search.model.FedexSchema;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.List;
import static au.com.agiledigital.idea_search.helpers.Utilities.getRows;
import java.util.HashMap;
import java.util.Map;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class TechnologyList {
  private SearchManager searchManager;
  private SettingsManager settingsManager;
  private static final Logger log = LoggerFactory.getLogger(TechnologyList.class);

  private final FedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();


  @Autowired
  public TechnologyList(FedexIdeaService fedexIdeaService) {
    this.searchManager = searchManager;
    this.settingsManager = settingsManager;
    this.fedexIdeaService = fedexIdeaService;
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
    FedexSchema schemaById = this.fedexIdeaService.listSchemas().get(0);

    Map schema = new HashMap<String , String>();

      schema.put("schema", DEFAULT_SCHEMA);
      schema.put("uiSchema", schemaById.getUiSchema());

    return this.gson.toJson(schema);
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
    @QueryParam("q") String searchString,
    @Context HttpServletResponse response
  ) {
    this.applyNoCacheHeaders(response);

    Set<String> newSet = new HashSet<>();
    newSet.add("fedex-ideas");

    List<IdeaContainer> allIdeas = getRows(newSet, "ds", this.searchManager, this.settingsManager);

    List<Map> preConvert = allIdeas.stream().map( idea -> {
      Map preJsonIdea = new HashMap<String, String>();
      preJsonIdea.put("title", idea.getTitle());
      preJsonIdea.put("url", idea.getUrl());
      preJsonIdea.put("description", idea.getDescription().getRenderedValue());
      preJsonIdea.put("technologies", idea.getTechnologies().getValue());
      preJsonIdea.put("owner", idea.getOwner().getRenderedValue());
      preJsonIdea.put("status", idea.getStatus().getRenderedValue());
      return preJsonIdea;
    }).collect(Collectors.toList());

    return allIdeas.isEmpty()
      ? "{[]}"
      : this.gson.toJson(preConvert);
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
