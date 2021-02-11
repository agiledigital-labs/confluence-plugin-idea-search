package au.com.agiledigital.idea_search.macros;

import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Macro for the Index Table. Fetches the pages with label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {
  private SearchManager searchManager;
  private PageBuilderService pageBuilderService;
  private SettingsManager settingsManager;
  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

  public IndexTable(
    @ComponentImport SearchManager searchManager,
    @ComponentImport PageBuilderService pageBuilderService,
    @ComponentImport SettingsManager settingsManager,
    @ComponentImport XhtmlContent xhtmlContent,
    DefaultFedexIdeaService fedexIdeaService) {
    this.searchManager = searchManager;
    this.pageBuilderService = pageBuilderService;
    this.settingsManager = settingsManager;

    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setValidating(false);
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-indexTable-macro-resource");

    // passing in an empty context as index table will be constructed with react
    Map<String, Object> context = new HashMap<>();

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
