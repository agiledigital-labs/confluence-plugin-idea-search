package au.com.agiledigital.idea_search.macros;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static au.com.agiledigital.idea_search.helpers.Utilities.DEFAULT_SCHEMA;

public class StructuredData implements Macro {

  private PageBuilderService pageBuilderService;
  private BootstrapManager bootstrapManager;
  private DefaultFedexIdeaService fedexIdeaService;

  @Autowired
  public StructuredData(
    @ComponentImport PageBuilderService pageBuilderService, @ComponentImport BootstrapManager bootstrapManager, DefaultFedexIdeaService fedexIdeaService) {
    this.pageBuilderService = pageBuilderService;
    this.bootstrapManager = bootstrapManager;
    this.fedexIdeaService = fedexIdeaService;
  }
  private static final Logger log = LoggerFactory.getLogger(StructuredData.class);

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {

    long macroContent = conversionContext.getEntity().getContentId().asLong();

//    FedexIdea currentIdea = this.fedexIdeaService.getByContentId(macroContent);

//    FedexSchema schema = this.fedexIdeaService.getSchema(currentIdea.getSchemaId());

    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredData-macro-resource");
    Map<String, Object> context = new HashMap<>();
    context.put("contextPath", bootstrapManager.getWebAppContextPath());
    context.put("schema", DEFAULT_SCHEMA);
//    context.put("uiSchema", schema.getUiSchema());
//    context.put("formData", currentIdea.getFormData());
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
