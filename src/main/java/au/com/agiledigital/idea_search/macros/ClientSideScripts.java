package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;

/** Loads client side javascript and css. */
public class ClientSideScripts implements Macro {

  private PageBuilderService pageBuilderService;

  public ClientSideScripts(@ComponentImport PageBuilderService pageBuilderService) {
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
      throws MacroExecutionException {
    pageBuilderService
        .assembler()
        .resources()
        .requireWebResource(
            "au.com.agiledigital.idea_search:ideaSearch-macro-clientSideScripts-macro-resource");

    return "";
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
