package au.com.agiledigital.idea_search.servlet;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TechnologyServletUnitTest {
  HttpServletRequest mockRequest;
  HttpServletResponse mockResponse;

  private FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
  private Gson gson = new Gson();

  @Before
  public void setup() {
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
  }

  @Test
  public void emptyTech() throws IOException {
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyServlet newServlet = new TechnologyServlet(ideaService);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);


    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(Collections.emptyList());
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    try {
      newServlet.doGet(mockRequest, mockResponse);
    } catch (Exception e){
      e.printStackTrace();
    }

    String expected = String.valueOf(this.gson.toJson(Collections.emptyList()));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void singleTech() throws IOException {
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyServlet newServlet = new TechnologyServlet(ideaService);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);


    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(Arrays.asList("perl"));
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    try {
      newServlet.doGet(mockRequest, mockResponse);
    } catch (Exception e){
      e.printStackTrace();
    }

    String expected = String.valueOf(this.gson.toJson(Arrays.asList("perl")));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }

  @Test
  public void multipleTech() throws IOException {
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    TechnologyServlet newServlet = new TechnologyServlet(ideaService);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);


    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(Arrays.asList("perl", "python"));
    Mockito.when(mockResponse.getWriter()).thenReturn(pw);

    try {
      newServlet.doGet(mockRequest, mockResponse);
    } catch (Exception e){
      e.printStackTrace();
    }

    String expected = String.valueOf(this.gson.toJson(Arrays.asList("perl", "python")));

    assertEquals(expected, sw.toString());
    pw.close();
    sw.close();
  }
}
