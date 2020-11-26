package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.dao.AoFedexTechnology;
import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(IdeaServletIntegrationTest.IdeaServletFuncTestDatabaseUpdater.class)
public class IdeaServletIntegrationTest {
  private EntityManager entityManager;
  private DefaultFedexIdeaService ideaService;
  private Gson gson = new Gson();
  @ComponentImport
  private UserAccessor userAccessor;

  private ActiveObjects ao;
  private FedexIdeaDao fedexIdeaDao;
  private IdeaServlet ideaServlet;
  HttpServletRequest mockRequest;
  HttpServletResponse mockResponse;


  HttpClient httpClient;
  String baseUrl;
  String servletUrl;
  private TestActiveObjects TestActiveObjects;


  @Before
  public void setup() {
    assertNotNull(entityManager);
    ao = new TestActiveObjects(entityManager);
    fedexIdeaDao = new FedexIdeaDao(ao, userAccessor);
    ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    ideaServlet = new IdeaServlet(ideaService);
    httpClient = new DefaultHttpClient();
    baseUrl = System.getProperty("baseurl");
    servletUrl = baseUrl + "/plugins/servlet/ideaservlet";
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    httpClient.getConnectionManager().shutdown();
  }

  @Test
  public void sortedTech() throws IOException, ServletException {
    ao.migrate(AoFedexTechnology.class);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    ideaServlet.doGet(mockRequest, mockResponse);

    Query query = Query.select("TECHNOLOGY").order("TECHNOLOGY ASC");
    AoFedexTechnology[] aoFedexTechnologies = this.ao.find(AoFedexTechnology.class, query);

    List<String> technologies = Arrays.stream(aoFedexTechnologies).map(t -> t.getTechnology()).collect(Collectors.toList());
    System.out.println(technologies);

    String expected = String.valueOf(this.gson.toJson(Arrays.asList("angular", "perl", "python")));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void distinctTech() throws IOException, ServletException {
    ao.migrate(AoFedexTechnology.class);

    final AoFedexTechnology aoFedexTechnologyPerl = ao.create(AoFedexTechnology.class);
    aoFedexTechnologyPerl.setTechnology("perl");
    aoFedexTechnologyPerl.save();

    final AoFedexTechnology aoFedexTechnologyAngular = ao.create(AoFedexTechnology.class);
    aoFedexTechnologyAngular.setTechnology("angular");
    aoFedexTechnologyAngular.save();

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    ideaServlet.doGet(mockRequest, mockResponse);

    Query query = Query.select("TECHNOLOGY").order("TECHNOLOGY ASC");
    AoFedexTechnology[] aoFedexTechnologies = this.ao.find(AoFedexTechnology.class, query);

    List<String> technologies = Arrays.stream(aoFedexTechnologies).map(t -> t.getTechnology()).collect(Collectors.toList());
    System.out.println(technologies);

    String expected = String.valueOf(this.gson.toJson(Arrays.asList("angular", "perl", "python")));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void emptyTech() throws IOException, ServletException {
    ao.migrate(AoFedexTechnology.class);
    ao.deleteWithSQL(AoFedexTechnology.class, "GLOBAL_ID > ?", 0);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    ideaServlet.doGet(mockRequest, mockResponse);

    Query query = Query.select("TECHNOLOGY").order("TECHNOLOGY ASC");
    AoFedexTechnology[] aoFedexTechnologies = this.ao.find(AoFedexTechnology.class, query);

    List<String> technologies = Arrays.stream(aoFedexTechnologies).map(t -> t.getTechnology()).collect(Collectors.toList());
    System.out.println(technologies);

    String expected = String.valueOf(this.gson.toJson(Arrays.asList()));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  public static class IdeaServletFuncTestDatabaseUpdater implements DatabaseUpdater {
    @Override
    public void update(EntityManager em) throws Exception {
      em.migrate(AoFedexTechnology.class);

      final AoFedexTechnology seedAoFedexTechnologyPython = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyPython.setTechnology("python");
      seedAoFedexTechnologyPython.save();

      final AoFedexTechnology seedAoFedexTechnologyPerl = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyPerl.setTechnology("perl");
      seedAoFedexTechnologyPerl.save();

      final AoFedexTechnology seedAoFedexTechnologyAngular = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyAngular.setTechnology("angular");
      seedAoFedexTechnologyAngular.save();
    }
  }
}
