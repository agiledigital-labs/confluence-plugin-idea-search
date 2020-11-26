package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.service.FedexIdeaService;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.google.gson.*;

public class IdeaServlet extends HttpServlet{
    private Gson gson = new Gson();

    private FedexIdeaService fedexIdeaService;


    public IdeaServlet(FedexIdeaService fedexIdeaService) {
        this.fedexIdeaService = fedexIdeaService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<String> allTechnologies = this.fedexIdeaService.techList();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(this.gson.toJson(allTechnologies));
    }
}