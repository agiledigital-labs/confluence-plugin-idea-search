package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StructuredField implements Macro {

  private PageBuilderService pageBuilderService;

  public StructuredField(
    @ComponentImport PageBuilderService pageBuilderService
  ) {
    this.pageBuilderService = pageBuilderService;
  }

  private String render(StructuredCategory category, String body) {
    Map<String, Object> test = new HashMap<>();


    switch (category) {
      case TECHNOLOGIES:
        test.put("payload", Arrays.asList(body.split(",")));
        break;
    }

    return VelocityUtils.getRenderedTemplate(
      "vm/" + category.getTemplate(),
      test
    );
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
