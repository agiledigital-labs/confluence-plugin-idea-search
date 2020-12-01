package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;

public class TechnologyServlet extends HttpServlet{
    private Gson gson = new Gson();

    private FedexIdeaService fedexIdeaService;


    public TechnologyServlet(FedexIdeaService fedexIdeaService) {
        this.fedexIdeaService = fedexIdeaService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<TechnologyAPI> allTechnologies = this.fedexIdeaService.techList();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(this.gson.toJson(allTechnologies));
    }
}