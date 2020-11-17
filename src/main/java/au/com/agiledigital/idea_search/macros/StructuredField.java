package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.macros.StructureFieldRenderHelper.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;

public class StructuredField implements Macro {

  private PageBuilderService pageBuilderService;

  public StructuredField(
    @ComponentImport PageBuilderService pageBuilderService
  ) {
    this.pageBuilderService = pageBuilderService;
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

    String stripped = s.replace("<p>", "").replace("</p>", "");

    return render(StructuredCategory.fromKey(map.get("category")), stripped);
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
