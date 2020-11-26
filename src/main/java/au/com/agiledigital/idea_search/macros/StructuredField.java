package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.helpers.StructureFieldRenderHelper.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;

/**
 * Macro for Structure Field data. Does body transformation for the category of structured field
 */
public class StructuredField implements Macro {

  private PageBuilderService pageBuilderService;
  private XhtmlContent xhtmlContent;

  public StructuredField(
    @ComponentImport PageBuilderService pageBuilderService,
    @ComponentImport XhtmlContent xhtmlContent
  ) {
    this.pageBuilderService = pageBuilderService;
    this.xhtmlContent = xhtmlContent;
  }

  @Override
  public String execute(
    Map<String, String> map,
    String s,
    ConversionContext conversionContext
  )
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredField-macro-resource"
      );
    StructuredCategory category = StructuredCategory.fromKey(
      map.get("category")
    );
    String stripped = s.replace("<p>", "").replace("</p>", "");

    return render(category, stripped);
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
