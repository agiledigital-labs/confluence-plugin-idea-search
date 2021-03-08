package au.com.agiledigital.structured_form.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Container macro for the page blueprint id
 */
public class TemplateId implements Macro {


  @Nonnull
  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    return ""; // This macro should not effect the display of the page.
  }

  @Nonnull
  @Override
  public BodyType getBodyType() {
    return BodyType.NONE;
  }

  @Nonnull
  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
