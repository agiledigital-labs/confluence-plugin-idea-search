package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

public class TechnologyServlet extends HttpServlet {

  private Gson gson = new Gson();
  private FedexIdeaService fedexIdeaService;

  public TechnologyServlet(FedexIdeaService fedexIdeaService) {
    this.fedexIdeaService = fedexIdeaService;
  }

  /**
   * Populate response with a list of distinct technologies
   * @param req HttpServletRequest coming through
   * @param resp HttpServletResponse to be populated with response data
   * @throws IOException exception
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    List<String> allTechnologies = this.fedexIdeaService.queryTechList();
    resp.setContentType(MediaType.APPLICATION_JSON);
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    resp.getWriter().write(this.gson.toJson(allTechnologies));
  }
}
