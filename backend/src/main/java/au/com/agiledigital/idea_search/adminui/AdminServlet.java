package au.com.agiledigital.idea_search.adminui;

import javax.inject.Inject;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.webresource.api.assembler.PageBuilderService;

public class AdminServlet extends HttpServlet
{
  @ComponentImport
  private final UserManager userManager;
  @ComponentImport
  private final LoginUriProvider loginUriProvider;
  @ComponentImport
  private final TemplateRenderer renderer;
  @ComponentImport
  private PageBuilderService pageBuilderService;

  @Inject
  public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, PageBuilderService pageBuilderService)
  {
    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-indexTable-macro-resource");

    String username = userManager.getRemoteUsername(request);
    if (username == null || !userManager.isSystemAdmin(username))
    {
      redirectToLogin(request, response);
      return;
    }

    response.setContentType("text/html;charset=utf-8");
    renderer.render("vm/admin.vm", response.getWriter());
  }

  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  private URI getUri(HttpServletRequest request)
  {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null)
    {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }
}