package ut. com.atlassian.tutorial;

import org.junit.Test;
import com.riseserver.tutorial.api.MyPluginComponent;
import com.riseserver.tutorial.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}