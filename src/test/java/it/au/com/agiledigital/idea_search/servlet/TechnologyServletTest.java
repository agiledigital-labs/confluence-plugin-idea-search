package it.au.com.agiledigital.idea_search.servlet;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.Assert.*;


public class TechnologyServletTest {
    HttpClient httpClient;
    String baseUrl;
    String servletUrl;
    private Gson gson = new Gson();

    @Before
    public void setup() {
        httpClient = new DefaultHttpClient();
        baseUrl = System.getProperty("baseurl");
        servletUrl = baseUrl + "/plugins/servlet/technology";
    }

    @After
    public void tearDown() {
        httpClient.getConnectionManager().shutdown();
    }

    @Test
    public void technologyServletTest() throws IOException {
        String expected = String.valueOf(this.gson.toJson(Arrays.asList("java", "js", "python", "ts")));

        // Given httpget is constructed with servlet url and there is a response handler.
        HttpGet httpget = new HttpGet(servletUrl);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        // When the request is made.
        String responseBody = httpClient.execute(httpget, responseHandler);

        // Then we should expect the servlet to return distinct technologies in ascending order.
        assertEquals(expected, responseBody.toString());
    }
}
