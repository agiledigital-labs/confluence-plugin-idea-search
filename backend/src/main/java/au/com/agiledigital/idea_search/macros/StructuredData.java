package au.com.agiledigital.idea_search.macros;

import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class StructuredData implements Macro {

  private final PageBuilderService pageBuilderService;
  private final DefaultFedexIdeaService fedexIdeaService;
  private Gson gson = new Gson();
  @Autowired
  public StructuredData(
    @ComponentImport PageBuilderService pageBuilderService, @ComponentImport BootstrapManager bootstrapManager, DefaultFedexIdeaService fedexIdeaService) {
    this.pageBuilderService = pageBuilderService;
    this.fedexIdeaService = fedexIdeaService;
  }
  private static final Logger log = LoggerFactory.getLogger(StructuredData.class);

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    Map<String,Object> data = new HashMap<String,Object>();
    data =gson.fromJson(s, data.getClass());

    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredData-macro-resource");
    Map<String, Object> context = new HashMap<>();
    context.put("schema", this.fedexIdeaService.getSchema(0));
    context.put("data", data);
    context.put("db-version", this.fedexIdeaService.getByContentId(conversionContext.getEntity().getContentId().asLong()));
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
