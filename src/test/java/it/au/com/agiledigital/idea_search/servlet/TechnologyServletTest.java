package it.au.com.agiledigital.idea_search.servlet;

import static org.junit.Assert.assertEquals;

import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
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
    servletUrl = System.getProperty("baseurl") + "/rest/idea/1/technology";
  }

  @After
  public void tearDown() {
    httpClient.getConnectionManager().shutdown();
  }

  /**
   * Should get a list of technologies when get request is invoked on test confluence instance.
   *
   * @throws IOException exception with input or writing outputs in servlet doGet
   */
  @Test
  public void technologyServletTest() throws IOException {
    String expectedTechList =
      this.gson.toJson(
          Arrays.asList(
            new TechnologyAPI("java"),
            new TechnologyAPI("js"),
            new TechnologyAPI("python"),
            new TechnologyAPI("ts")
          )
        );

    // Given httpget is constructed with servlet url and there is a response handler.
    HttpGet httpget = new HttpGet(servletUrl);
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
      // add Authorization param
      String authStr = "admin:admin";
      byte[] authEncBytes = Base64.encodeBase64(authStr.getBytes());
      String authStringEnc = new String(authEncBytes);
      httpget.setHeader("Authorization", "Basic " + authStringEnc);
      httpget.setHeader("X-Atlassian-Token", "no-check ");
    // When the request is made.
    String responseBody = httpClient.execute(httpget, responseHandler);

    // Then we should expect the servlet to return distinct technologies in ascending order.
    assertEquals(expectedTechList, responseBody.toString());
  }
}
