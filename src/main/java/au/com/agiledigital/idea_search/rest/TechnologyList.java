package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.List;

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
   * Added to prevent the search caching the responses
   *
   * @param response
   */
  private void applyNoCacheHeaders(HttpServletResponse response) {
    CachingHeaders.PREVENT_CACHING.apply(response);
  }
}
