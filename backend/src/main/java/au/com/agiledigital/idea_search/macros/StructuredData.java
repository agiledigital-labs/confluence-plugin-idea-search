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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.WordUtils;

public class StructuredData implements Macro {
  private final PageBuilderService pageBuilderService;
  private Gson gson = new Gson();

  @Autowired
  public StructuredData(
    @ComponentImport PageBuilderService pageBuilderService, @ComponentImport BootstrapManager bootstrapManager, DefaultFedexIdeaService fedexIdeaService) {
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    Map<String, String> data = new LinkedHashMap<>();
    data =gson.fromJson(Jsoup.parse(s).body().text(), data.getClass());
    Map<String, String> renderData = new LinkedHashMap<>();

    data.forEach( (r, t) -> {
      renderData.put(WordUtils.capitalize(r.replaceAll("[A-Z]", " $0")), t);
    });

    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-structuredData-macro-resource");

    Map<String, Object> context = new HashMap<>();
    context.put("data", renderData);
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