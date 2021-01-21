package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.assembler.DefaultPageBuilderService;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import java.util.HashMap;
import java.util.Map;

import static au.com.agiledigital.idea_search.helpers.StructureFieldRenderHelper.render;

public class StructuredData implements Macro {

  private PageBuilderService pageBuilderService;
  private BootstrapManager bootstrapManager;

  public StructuredData(
    @ComponentImport PageBuilderService pageBuilderService, @ComponentImport BootstrapManager bootstrapManager) {
    this.pageBuilderService = pageBuilderService;
    this.bootstrapManager = bootstrapManager;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredData-macro-resource");
    Map<String, Object> context = new HashMap<>();
    context.put("contextPath", bootstrapManager.getWebAppContextPath() );
    return VelocityUtils.getRenderedTemplate("vm/StructuredData.vm", context);
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
