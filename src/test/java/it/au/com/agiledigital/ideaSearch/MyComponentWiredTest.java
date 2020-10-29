package it.au.com.agiledigital.ideaSearch;

import static org.junit.Assert.assertEquals;

import au.com.agiledigital.ideaSearch.api.MyPluginComponent;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.sal.api.ApplicationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AtlassianPluginsTestRunner.class)
public class MyComponentWiredTest {

  private final ApplicationProperties applicationProperties;
  private final MyPluginComponent myPluginComponent;

  public MyComponentWiredTest(
    ApplicationProperties applicationProperties,
    MyPluginComponent myPluginComponent
  ) {
    this.applicationProperties = applicationProperties;
    this.myPluginComponent = myPluginComponent;
  }

  @Test
  public void testMyName() {
    assertEquals(
      "names do not match!",
      "myComponent:" + applicationProperties.getDisplayName(),
      myPluginComponent.getName()
    );
  }
}
