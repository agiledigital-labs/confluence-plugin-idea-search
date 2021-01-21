package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.dao.AoFedexTechnology;
import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import au.com.agiledigital.idea_search.rest.TechnologyList;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TechnologyServlete2eTest.TechnologyServletFuncTestDatabaseUpdater.class)
public class TechnologyServlete2eTest {

  private EntityManager entityManager;
  private DefaultFedexIdeaService ideaService;
  private Gson gson = new Gson();

  @ComponentImport
  private UserAccessor userAccessor;

  private ActiveObjects ao;
  private FedexIdeaDao fedexIdeaDao;
  private TechnologyList technologyList;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private HttpClient httpClient;
  private String baseUrl;
  private TestActiveObjects TestActiveObjects;

  @Before
  public void setup() {
    assertNotNull(entityManager);
    ao = new TestActiveObjects(entityManager);
    fedexIdeaDao = new FedexIdeaDao(ao, userAccessor);
    ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    technologyList = new TechnologyList(ideaService);
    httpClient = new DefaultHttpClient();
    baseUrl = System.getProperty("baseurl");
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    httpClient.getConnectionManager().shutdown();
  }

  /**
   * Should return a sorted list of technologies
   *
   * @throws IOException exception with input or writing outputs in servlet doGet
   */
  @Test
  public void sortedTech() throws IOException {
    String sortedTech =
      this.gson.toJson(
        Arrays.asList(
          new TechnologyAPI("angular"),
          new TechnologyAPI("perl"),
          new TechnologyAPI("python")
        )
      );
    ao.migrate(AoFedexTechnology.class);

    // Given the servlet writes response on supplied response object.

    // When we call the servlet function to retrieve a list of technologies.
    String response = technologyList.getTechList("", mockResponse);

    // Then we should get a list of sorted technologies.
    assertEquals(sortedTech, response);
  }

  /**
   * Should return a distinct list of technologies
   *
   * @throws IOException exception with input or writing outputs in servlet doGet
   */
  @Test
  public void distinctTech() throws IOException {
    String distinctTech =
      this.gson.toJson(
        Arrays.asList(
          new TechnologyAPI("angular"),
          new TechnologyAPI("perl"),
          new TechnologyAPI("python")
        )
      );
    ao.migrate(AoFedexTechnology.class);

    // Given there are duplicate technologies in the database and servlet writes response on
    // supplied response object.
    final AoFedexTechnology aoFedexTechnologyPerl = ao.create(AoFedexTechnology.class);
    aoFedexTechnologyPerl.setTechnology("perl");
    aoFedexTechnologyPerl.save();

    final AoFedexTechnology aoFedexTechnologyAngular = ao.create(AoFedexTechnology.class);
    aoFedexTechnologyAngular.setTechnology("angular");
    aoFedexTechnologyAngular.save();

    // When we call the servlet function to retrieve a list of technologies.
    String response = technologyList.getTechList("", mockResponse);

    // Then we should get a list of distinct technologies.
    assertEquals(distinctTech, response);
  }

  /**
   * Class to seed database before test. Adds python, perl and angular in respective order to the
   * test database.
   */
  public static class TechnologyServletFuncTestDatabaseUpdater implements DatabaseUpdater {

    @Override
    public void update(EntityManager em) throws Exception {
      em.migrate(AoFedexTechnology.class);

      AoFedexTechnology seedAoFedexTechnologyPython = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyPython.setTechnology("python");
      seedAoFedexTechnologyPython.save();

      AoFedexTechnology seedAoFedexTechnologyPerl = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyPerl.setTechnology("perl");
      seedAoFedexTechnologyPerl.save();

      AoFedexTechnology seedAoFedexTechnologyAngular = em.create(AoFedexTechnology.class);
      seedAoFedexTechnologyAngular.setTechnology("angular");
      seedAoFedexTechnologyAngular.save();
    }
  }
}
