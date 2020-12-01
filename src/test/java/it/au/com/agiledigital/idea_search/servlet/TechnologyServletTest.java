package it.au.com.agiledigital.idea_search.servlet;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TechnologyServletTest {

  private HttpClient httpClient;
  private String servletUrl;
  private Gson gson = new Gson();

  @Before
  public void setup() {
    httpClient = new DefaultHttpClient();
    servletUrl = System.getProperty("baseurl") + "/plugins/servlet/technology";
  }

  @After
  public void tearDown() {
    httpClient.getConnectionManager().shutdown();
  }

  /**
   * Should get a list of technologies when get request is invoked on test confluence instance.
   * @throws IOException exception with input or writing outputs in servlet doGet
   */
  @Test
  public void technologyServletTest() throws IOException {
    String expectedTechList =
      this.gson.toJson(Arrays.asList("java", "js", "python", "ts"));

    // Given httpget is constructed with servlet url and there is a response handler.
    HttpGet httpget = new HttpGet(servletUrl);
    ResponseHandler<String> responseHandler = new BasicResponseHandler();

    // When the request is made.
    String responseBody = httpClient.execute(httpget, responseHandler);

    // Then we should expect the servlet to return distinct technologies in ascending order.
    assertEquals(expectedTechList, responseBody.toString());
  }
}
