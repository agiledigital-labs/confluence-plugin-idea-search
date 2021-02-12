package au.com.agiledigital.idea_search.macros;


import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Macro for the Index Table. Fetches the pages with label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {


  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {

    // passing in an empty context as index table will be constructed with react
    Map<String, Object> context = new HashMap<>();

    return VelocityUtils.getRenderedTemplate("vm/IndexPage.vm", context);
  }

  @Override
  public BodyType getBodyType() {
    return BodyType.NONE;
  }

  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
