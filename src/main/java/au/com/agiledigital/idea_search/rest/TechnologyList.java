package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.google.gson.Gson;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External rest API servlet
 */
@Path("/")
@Component
public class TechnologyList {

  private static final Logger log = LoggerFactory.getLogger(TechnologyList.class);

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
   *
   * @param searchString to find technologies that begin with this string
   * @param response Servlet contest
   * @return String in the form of a json list of TechnologyAPI objects
   */
  @Path("/technology")
  @Produces({ "application/json" })
  @GET
  public String getTechList(
    @QueryParam("q") String searchString,
    @Context HttpServletResponse response
  ) {
    String normalizeSearch = searchString != null && searchString.length() > 0
      ? cleanTextContent(searchString).toLowerCase()
      : null;
    this.applyNoCacheHeaders(response);

    List<TechnologyAPI> allTechnologies = normalizeSearch == null ||
      normalizeSearch.length() == 0
      ? this.fedexIdeaService.queryTechList()
      : this.fedexIdeaService.queryTechList(normalizeSearch);

    log.warn("\n\n\n***All techs are here***\n\n\n");
    log.warn("The techs are: "+allTechnologies);
    log.warn("Are you empty? "+allTechnologies.isEmpty());
    log.warn("Are you null? "+ String.valueOf(allTechnologies == null));

    if (allTechnologies !=null && allTechnologies.isEmpty() && normalizeSearch.endsWith(",")) {
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
   * @param response
   */
  private void applyNoCacheHeaders(HttpServletResponse response) {
    CachingHeaders.PREVENT_CACHING.apply(response);
  }
}
