package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.helpers.MacroHelpers.splitTrimToSet;
import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;

import au.com.agiledigital.idea_search.macros.transport.BlueprintContainer;
import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;

/**
 * Macro for the Index Table. Fetches the pages with the label "fedex-ideas" from the space
 * specified, pulls the structured field macro from each and processes the data. It constructs a
 * table to display said data.
 */
public class IndexTable implements Macro {

  private SearchManager searchManager;
  private PageBuilderService pageBuilderService;
  private SettingsManager settingsManager;
  private XhtmlContent xhtmlContent;
  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private BlueprintManager blueprintManager;
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
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      String nodeName = node.getAttributes().getNamedItem("ac:name").getNodeValue();
      if (nodeName.equals("Idea Structured Field") || nodeName.equals("Blueprint Id Storage")) {
        Node child = node.getFirstChild();
        do {
          if (child instanceof Element
            && child.getNodeName().equals("ac:parameter")
            && child.getTextContent().equals(category.getKey())) {
            return new MacroRepresentation(node, category, serializer, xhtmlContent);
          }
        } while ((child = child.getNextSibling()) != null);
      }
    }

    return null;
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
    Set<String> labels = new HashSet<String>(splitTrimToSet(parameters.get("labels"), ","));
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
        new SubsetResultFilter(20));

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
                row.title = page.getTitle();
                row.url =
                  settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath();

                Arrays.asList(StructuredCategory.values())
                  .forEach(
                    category ->
                      row.setMacroRepresentations(
                        category, getMacroFromList(macros, category, serializer)));

                return row;
              } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
              }

              return null;
            })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (InvalidSearchException e) {
      e.printStackTrace();
    }

    Stream<IdeaContainer> filteredRows =
      rows.stream()
        .filter(container -> container.blueprintId != null && !container.blueprintId.isEmpty());

    context.put("rows", rows);
    context.put(
      "blueprint",
      new BlueprintContainer(
        conversionContext.getSpaceKey(),
        settingsManager.getGlobalSettings().getBaseUrl(),
        filteredRows.count() == 0
          // Set the blueprint id to be that of fedex idea blueprint
          ? this.fedexIdeaService.getBlueprintId()
          : Collections.max(
            filteredRows
              .collect(
                Collectors.groupingBy(
                  ideaContainer -> ideaContainer.blueprintId,
                  Collectors.counting()))
              .entrySet(),
            Comparator.comparing(Entry::getValue))
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
