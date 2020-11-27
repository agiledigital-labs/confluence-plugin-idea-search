package au.com.agiledigital.idea_search.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
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

  HttpServletRequest mockRequest;
  HttpServletResponse mockResponse;

  private FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
  FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
  TechnologyServlet technologyServlet = new TechnologyServlet(ideaService);
  private Gson gson = new Gson();

  @Before
  public void setup() {
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
  }

  @Test
  public void emptyTech() throws IOException {
    String expected = String.valueOf(this.gson.toJson(Collections.emptyList()));

    // Given that dao returns empty list of technologies and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito
      .when(fedexIdeaDao.techDaoList())
      .thenReturn(Collections.emptyList());
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    technologyServlet.doGet(mockRequest, mockResponse);

    // Then we should get an empty list.
    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void singleTech() throws IOException {
    String expected = String.valueOf(this.gson.toJson(Arrays.asList("perl")));

    // Given that dao returns one technology and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(Arrays.asList("perl"));
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    technologyServlet.doGet(mockRequest, mockResponse);

    // Then we should get a list with one technology.
    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void multipleTech() throws IOException {
    String expected = String.valueOf(
      this.gson.toJson(Arrays.asList("perl", "python"))
    );

    // Given that dao returns multiple technologies and servlet writes response on supplied response object.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    Mockito
      .when(fedexIdeaDao.techDaoList())
      .thenReturn(Arrays.asList("perl", "python"));
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    // When we call the servlet function to retrieve a list of technologies.
    technologyServlet.doGet(mockRequest, mockResponse);

    // Then we should get a list with multiple technologies.
    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }
}
