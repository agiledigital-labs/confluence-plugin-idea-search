package au.com.agiledigital.idea_search.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import au.com.agiledigital.idea_search.rest.TechnologyList;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TechnologyServletUnitTest {

  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private Gson gson = new Gson();

  @Before
  public void setup() {
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
  }

  /*
   * Isolating test cases as test data specific method call mocks are required.
   * Due to the nature of invokation of getWriter method with HttpServletResponse object,
   * passing in a mocked printWriter causes nullPointerError during invokation.
   * A new printWriter is constructed and passed on as the mocked return of getWriter.
   */

  /**
   * Should write an empty list in json in response, based on get technology mocks.
   * @throws IOException exception with input or writing outputs in servlet getTechList
   */
  @Test
  public void noTech() throws IOException {
    FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyList technologyList = new TechnologyList(ideaService);
    String noTech = this.gson.toJson(Arrays.asList(new TechnologyAPI("t")));

    // Given that dao returns empty list of technologies and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito
      .when(fedexIdeaDao.queryTechList())
      .thenReturn(Collections.emptyList());
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    String test = technologyList.getTechList("t,", mockResponse);

    // Then we should get an empty list.
    assertEquals(noTech, test);
    pw.close();
    sw.close();
  }

  /**
   * Should write a single tech in response.
   * @throws IOException exception with input or writing outputs in servlet getTechList
   */
  @Test
  public void singleTech() throws IOException {
    FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyList technologyList = new TechnologyList(ideaService);
    String singleTech =
      this.gson.toJson(Arrays.asList(new TechnologyAPI("perl")));

    // Given that dao returns one technology and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito
      .when(fedexIdeaDao.queryTechList())
      .thenReturn(Arrays.asList(new TechnologyAPI("perl")));
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    String test = technologyList.getTechList("", mockResponse);

    // Then we should get a list with one technology.
    assertEquals(singleTech, test);
    pw.close();
    sw.close();
  }

  /**
   * Should write multiple techs in response.
   * @throws IOException exception with input or writing outputs in servlet getTechList
   */
  @Test
  public void multipleTech() throws IOException {
    FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyList technologyList = new TechnologyList(ideaService);
    String multipleTech =
      this.gson.toJson(
          Arrays.asList(new TechnologyAPI("perl"), new TechnologyAPI("python"))
        );

    // Given that dao returns multiple technologies and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito
      .when(fedexIdeaDao.queryTechList())
      .thenReturn(
        Arrays.asList(new TechnologyAPI("perl"), new TechnologyAPI("python"))
      );
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    String test = technologyList.getTechList("", mockResponse);

    // Then we should get a list with multiple technologies.
    assertEquals(multipleTech, test);
    pw.close();
    sw.close();
  }
}
