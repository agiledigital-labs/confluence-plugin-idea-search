package ut.au.com.agiledigital;

import static org.junit.Assert.assertEquals;

import au.com.agiledigital.api.MyPluginComponent;
import au.com.agiledigital.impl.MyPluginComponentImpl;
import org.junit.Test;

public class MyComponentUnitTest {

  @Test
  public void testMyName() {
    MyPluginComponent component = new MyPluginComponentImpl(null);
    assertEquals("names do not match!", "myComponent", component.getName());
  }
}
