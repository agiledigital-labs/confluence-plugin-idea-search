package au.com.agiledigital.idea_search.adminui;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * Servlet to serve a admin configuration page on Configuration UI.
 * See more at: https://developer.atlassian.com/server/confluence/adding-a-configuration-ui-for-your-plugin/
 */
public class AdminServlet extends HttpServlet
{
  @ComponentImport
  private final UserManager userManager;
  @ComponentImport
  private final LoginUriProvider loginUriProvider;
  @ComponentImport
  private final TemplateRenderer renderer;
  @ComponentImport
  private final PageBuilderService pageBuilderService;

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
    // require web resource to be able to use custom react
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaAdminResource");

    // verify that admin user is requesting the page
    String username = userManager.getRemoteUsername(request);
    if (username == null || !userManager.isSystemAdmin(username))
    {
      redirectToLogin(request, response);
      return;
    }

    // render admin configuration ui page
    response.setContentType("text/html;charset=utf-8");
    renderer.render("vm/Admin.vm", response.getWriter());
  }

  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  // gets the uri to identify where a login attempt is being made
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