package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.helpers.MacroHelpers.splitTrimToSet;
import static au.com.agiledigital.idea_search.helpers.Utilities.getRows;

import au.com.agiledigital.idea_search.macros.transport.BlueprintContainer;
import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Macro for the Index Table. Fetches the pages with the label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {
  private SearchManager searchManager;
  private PageBuilderService pageBuilderService;
  private SettingsManager settingsManager;
  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private final DefaultFedexIdeaService fedexIdeaService;

  public IndexTable(
    @ComponentImport SearchManager searchManager,
    @ComponentImport PageBuilderService pageBuilderService,
    @ComponentImport SettingsManager settingsManager,
    @ComponentImport XhtmlContent xhtmlContent,
    DefaultFedexIdeaService fedexIdeaService) {
    this.searchManager = searchManager;
    this.pageBuilderService = pageBuilderService;
    this.settingsManager = settingsManager;
    this.fedexIdeaService = fedexIdeaService;

    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setValidating(false);
  }

  private Set<String> getMacroLabels(Map<String, String> parameters) {
    Set<String> labels = new HashSet<>(splitTrimToSet(parameters.get("labels"), ","));
    labels.add("fedex-ideas");

    return labels;
  }


  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.idea_search:ideaSearch-macro-indexTable-macro-resource");

    Map<String, Object> context = new HashMap<>();

    List<IdeaContainer> rows = getRows(getMacroLabels(map), conversionContext.getSpaceKey(), this.searchManager, this.settingsManager);

    List<IdeaContainer> filteredRows =
      rows.stream()
        .filter(container -> container.getBlueprintId() != null && !container.getBlueprintId().isEmpty()).collect(Collectors.toList());

    context.put("rows", rows);
    context.put(
      "blueprint",
      new BlueprintContainer(
        conversionContext.getSpaceKey(),
        settingsManager.getGlobalSettings().getBaseUrl(),
        filteredRows.isEmpty()
          // Set the blueprint id to be that of fedex idea blueprint
          ? this.fedexIdeaService.getBlueprintId()
          : Collections.max(
          filteredRows.stream()
            .collect(
              Collectors.groupingBy(
                IdeaContainer::getBlueprintId,
                Collectors.counting()))
            .entrySet(),
          Entry.comparingByValue())
          .getKey()));

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
