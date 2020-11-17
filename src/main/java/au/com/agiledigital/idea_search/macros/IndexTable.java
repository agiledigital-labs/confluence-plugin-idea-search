package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.macros.MacroHelpers.splitTrimToSet;

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
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IndexTable implements Macro {

  private SearchManager searchManager;
  private PageBuilderService pageBuilderService;
  private SettingsManager settingsManager;
  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

  public IndexTable(@ComponentImport SearchManager searchManager,
    @ComponentImport PageBuilderService pageBuilderService,
    @ComponentImport SettingsManager settingsManager) {
    this.searchManager = searchManager;
    this.pageBuilderService = pageBuilderService;
    this.settingsManager = settingsManager;

    documentBuilderFactory.setNamespaceAware(false);
    documentBuilderFactory.setValidating(false);
  }

  private Document parseXML(String xml)
    throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

    return builder.parse(new ByteArrayInputStream(xml.getBytes()));
  }

  private MacroRepresentation getMacroFromList(NodeList macros, StructuredCategory category) {
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      if (!node.getAttributes().getNamedItem("ac:name").getNodeValue()
        .equals("Idea Structured Field")) {
        continue;
      }

      Node child = node.getFirstChild();
      do {
        if (child instanceof Element && child.getNodeName().equals("ac:parameter") && child
          .getTextContent().equals(category.getKey())) {
          return new MacroRepresentation(node, category);
        }
      } while ((child = child.getNextSibling()) != null);
    }

    return null;
  }

  private BooleanQuery getLabelQuery(Set<String> labels) {
    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    labels.forEach((label) -> booleanQueryFactory.addShould(new LabelQuery(label)));

    return booleanQueryFactory.toBooleanQuery();
  }

  private String wrapBody(String body) {
    return "<ac:confluence>" + body + "</ac:confluence>";
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

    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    Set<String> labels = new HashSet<String>(splitTrimToSet(map.get("labels"), ","));
    labels.add("fedex-ideas");
    booleanQueryFactory.addMust(getLabelQuery(labels));
    booleanQueryFactory.addMust(new ContentTypeQuery(ContentTypeEnum.PAGE));
    booleanQueryFactory.addMust(new InSpaceQuery(conversionContext.getSpaceKey()));

    SearchFilter searchFilter = ContentPermissionsSearchFilter.getInstance().and(
      SpacePermissionsSearchFilter.getInstance());
    ContentSearch search = new ContentSearch(booleanQueryFactory.toBooleanQuery(),
      ModifiedSort.DESCENDING, searchFilter, new SubsetResultFilter(20));

    List<IdeaRow> rows;

    try {
      List<Searchable> maybePages = this.searchManager
        .searchEntities(search, EntityVersionPolicy.LATEST_VERSION);

      rows = maybePages.stream().filter((entity) -> entity instanceof AbstractPage).map(entity -> {
        AbstractPage page = (AbstractPage) entity;

        BodyContent content = page.getBodyContent();

        try {
          Document bodyParsed = parseXML(wrapBody(content.getBody()));
          NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");

          IdeaRow row = new IdeaRow();
          row.technologies = getMacroFromList(macros, StructuredCategory.TECHNOLOGIES);
          row.title = page.getTitle();
          row.url = settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath();

          return row;
        } catch (ParserConfigurationException | IOException | SAXException e) {
          e.printStackTrace();
        }

        return null;
      }).filter((entry) -> entry != null).collect(Collectors.toList());

    } catch (InvalidSearchException e) {
      e.printStackTrace();
      rows = Collections.emptyList();
    }

    context.put("rows", rows);

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
