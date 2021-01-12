package au.com.agiledigital.idea_search.macros;

import au.com.agiledigital.idea_search.macros.transport.BlueprintContainer;
import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFilter;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchManager.EntityVersionPolicy;
import com.atlassian.confluence.search.v2.filter.SubsetResultFilter;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.searchfilter.ContentPermissionsSearchFilter;
import com.atlassian.confluence.search.v2.searchfilter.SpacePermissionsSearchFilter;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static au.com.agiledigital.idea_search.helpers.MacroHelpers.splitTrimToSet;
import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;
import static au.com.agiledigital.idea_search.helpers.Utilities.getMacroRepresentation;

/**
 * Macro for the Index Table. Fetches the pages with the label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {

  private static final Logger log = LoggerFactory.getLogger(IndexTable.class);

  private SearchManager searchManager;
  private PageBuilderService pageBuilderService;
  private SettingsManager settingsManager;
  private XhtmlContent xhtmlContent;
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
    this.xhtmlContent = xhtmlContent;
    this.fedexIdeaService = fedexIdeaService;

    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setValidating(false);
  }

  /**
   * Parses XML to Java Dom objects
   *
   * @param xml string of readin XML content
   * @return Object containing the structure of the XML which has functionality for navigating the
   * dom
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  private Document parseXML(String xml)
    throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

    return builder.parse(new ByteArrayInputStream(xml.getBytes()));
  }

  /**
   * Finds a Structured Field macro with a category from a list of macros
   *
   * @param macros     Structured Field macro list
   * @param category   The needle for the search
   * @param serializer XML serialiser
   * @return Representation of a macro from Confluence Storage format
   */
  private MacroRepresentation getMacroFromList(
    NodeList macros, StructuredCategory category, LSSerializer serializer) {
    return getMacroRepresentation(macros, category, serializer, xhtmlContent);
  }

  /**
   * Creates a confluence searchable query to find pages of a certain type
   *
   * @param labels   list of page labels
   * @param spaceKey location of page search in confluence
   * @return Confluence searchable query
   */
  private BooleanQuery createSearchableQuery(Set<String> labels, String spaceKey) {
    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    booleanQueryFactory.addMust(getLabelQuery(labels));
    booleanQueryFactory.addMust(new ContentTypeQuery(ContentTypeEnum.PAGE));
    booleanQueryFactory.addMust(new InSpaceQuery(spaceKey));

    return booleanQueryFactory.toBooleanQuery();
  }

  private Set<String> getMacroLabels(Map<String, String> parameters) {
    Set<String> labels = new HashSet<>(splitTrimToSet(parameters.get("labels"), ","));
    labels.add("fedex-ideas");

    return labels;
  }

  /**
   * Query for Confluence page labels
   *
   * @param labels List of labels
   * @return Confluence searchable query for labels
   */
  private BooleanQuery getLabelQuery(Set<String> labels) {
    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    labels.forEach(label -> booleanQueryFactory.addShould(new LabelQuery(label)));

    return booleanQueryFactory.toBooleanQuery();
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

    SearchFilter searchFilter =
      ContentPermissionsSearchFilter.getInstance()
        .and(SpacePermissionsSearchFilter.getInstance());
    ContentSearch search =
      new ContentSearch(
        createSearchableQuery(getMacroLabels(map), conversionContext.getSpaceKey()),
        ModifiedSort.DESCENDING,
        searchFilter,
        new SubsetResultFilter(100));

    List<IdeaContainer> rows = Collections.emptyList();

    try {
      List<Searchable> maybePages =
        this.searchManager.searchEntities(search, EntityVersionPolicy.LATEST_VERSION);

      rows =
        maybePages.stream()
          .filter(entity -> entity instanceof AbstractPage)
          .map(
            entity -> {
              AbstractPage page = (AbstractPage) entity;
              BodyContent content = page.getBodyContent();

              try {
                Document bodyParsed = parseXML(wrapBody(content.getBody()));
                NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");
                DOMImplementationLS ls = (DOMImplementationLS) bodyParsed.getImplementation();
                LSSerializer serializer = ls.createLSSerializer();

                IdeaContainer row = new IdeaContainer();
                row.setTitle( page.getTitle());
                row.setUrl(
                  settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath());

                Arrays.asList(StructuredCategory.values())
                  .forEach(
                    category ->
                      row.setMacroRepresentations(
                        category, getMacroFromList(macros, category, serializer)));

                return row;
              } catch (ParserConfigurationException | IOException | SAXException e) {
                log.warn(e.toString());
              }

              return null;
            })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (InvalidSearchException e) {
      log.warn(e.toString());
    }

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
