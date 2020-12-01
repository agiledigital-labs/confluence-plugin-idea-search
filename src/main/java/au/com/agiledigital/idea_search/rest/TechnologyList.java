package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.service.FedexIdeaService;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Path("/")
@Component
public class TechnologyList {
    private final FedexIdeaService fedexIdeaService;
    private Gson gson = new Gson();

    Logger log = LoggerFactory.getLogger(TechnologyList.class);

//    private TechnologyList() {
//        this.fedexIdeaService = null;
//    }

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
        return text.replaceAll("[^\\x00-\\x7F]", "")
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\p{C}", "");

    }



    @Path("/technology")
    @Produces({"application/json"})
    @GET
    public String test(@QueryParam("q") String searchString, @Context HttpServletResponse response) {

        String normalizeSearch = searchString != null && searchString.length() > 0 ? cleanTextContent(searchString).toLowerCase() : null;
        log.warn("Doing a search for" + normalizeSearch);
        this.applyNoCacheHeaders(response);

        List<TechnologyAPI> allTechnologies = normalizeSearch == null || normalizeSearch.length() == 0 ?
                this.fedexIdeaService.techList()
                :
                this.fedexIdeaService.techList(normalizeSearch);

        if(allTechnologies.isEmpty() && normalizeSearch.endsWith(",")){
            TechnologyAPI newTech = new TechnologyAPI(normalizeSearch.replace(",",""));
            allTechnologies.add(0, newTech);
        }


        return this.gson.toJson(allTechnologies);
    }
    private void applyNoCacheHeaders(HttpServletResponse response) {
        CachingHeaders.PREVENT_CACHING.apply(response);
    }
}
