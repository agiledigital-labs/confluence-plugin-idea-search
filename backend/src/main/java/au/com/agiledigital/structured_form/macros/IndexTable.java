package au.com.agiledigital.structured_form.macros;


import au.com.agiledigital.structured_form.macros.transport.BlueprintContainer;
import au.com.agiledigital.structured_form.service.DefaultFormDataService;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
/**
 * Macro for the Index Table. Fetches the pages with label "form-data" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {
  private final PageBuilderService pageBuilderService;
  private final DefaultFormDataService formDataService;
  @ComponentImport
  private SettingsService settingsService;
  public IndexTable(
    @ComponentImport PageBuilderService pageBuilderService, DefaultFormDataService formDataService,
    @Qualifier("settingsService")
    @ComponentImport SettingsService settingsService) {
    this.pageBuilderService = pageBuilderService;
    this.formDataService = formDataService;
    this.settingsService = settingsService;
  }

  @Override
  public String execute(Map<String, String> map, String s, @Nonnull ConversionContext conversionContext )
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.structured_form:entrypoint-indexTable");
    // passing in an empty context as index table will be constructed with react
    Map<String, Object> context = new HashMap<>();
    context.put(
      "blueprint",
      new BlueprintContainer(
        conversionContext.getSpaceKey(),
        this.settingsService.getGlobalSettings().getBaseUrl(),
         this.formDataService.getBlueprintId()));
          // Set the blueprint id to be that of form data blueprint

    return VelocityUtils.getRenderedTemplate("vm/IndexPage.vm", context);
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
