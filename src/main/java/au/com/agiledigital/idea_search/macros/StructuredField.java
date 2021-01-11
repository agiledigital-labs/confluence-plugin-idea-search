package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import java.util.Map;

import static au.com.agiledigital.idea_search.helpers.StructureFieldRenderHelper.render;

/**
 * Macro for Structure Field data. Does body transformation for the category of structured field
 */
public class StructuredField implements Macro {

  private PageBuilderService pageBuilderService;

  public StructuredField(
    @ComponentImport PageBuilderService pageBuilderService) {
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredField-macro-resource");

    return render(StructuredCategory.fromKey(map.get("category")), s);
  }

  @Override
  public BodyType getBodyType() {
    return BodyType.RICH_TEXT;
  }

  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
