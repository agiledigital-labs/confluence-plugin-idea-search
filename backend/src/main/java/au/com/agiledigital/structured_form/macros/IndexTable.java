package au.com.agiledigital.structured_form.macros;


import au.com.agiledigital.structured_form.macros.transport.BlueprintContainer;
import au.com.agiledigital.structured_form.service.DefaultFormDataService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import java.util.HashMap;
import java.util.Map;
/**
 * Macro for the Index Table. Fetches the pages with label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {
  private final PageBuilderService pageBuilderService;
  private final DefaultFormDataService formDataService;
  @ComponentImport
  SettingsManager settingsManager;
  public IndexTable(
    @ComponentImport PageBuilderService pageBuilderService, DefaultFormDataService formDataService, @ComponentImport SettingsManager settingsManager) {
    this.pageBuilderService = pageBuilderService;
    this.formDataService = formDataService;
    this.settingsManager = settingsManager;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext )
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.structured_form:ideaSearch-macro-indexTable-macro-resource");
    // passing in an empty context as index table will be constructed with react
    Map<String, Object> context = new HashMap<>();
    context.put(
      "blueprint",
      new BlueprintContainer(
        conversionContext.getSpaceKey(),
        settingsManager.getGlobalSettings().getBaseUrl(),
         this.formDataService.getBlueprintId()));
          // Set the blueprint id to be that of fedex idea blueprint

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
