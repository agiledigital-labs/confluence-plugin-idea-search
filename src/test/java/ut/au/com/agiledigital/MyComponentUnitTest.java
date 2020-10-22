package ut.au.com.agiledigital;

import org.junit.Test;
import au.com.agiledigital.api.MyPluginComponent;
import au.com.agiledigital.impl.MyPluginComponentImpl;

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