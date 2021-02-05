package au.com.agiledigital.idea_search.helpers;

import au.com.agiledigital.idea_search.macros.DataMacroRepresentation;
import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;

import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.BodyContent;
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
import com.atlassian.confluence.user.DefaultUserAccessor;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class Utilities {
  private static final Logger log = LoggerFactory.getLogger(Utilities.class);
  private static XhtmlContent xhtmlContent;
  private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

  private Utilities() {throw new IllegalStateException("Utility class"); }

  /**
   * Remove tags and give only wrapped text
   *
   * @param rawData the string to be stripped off tags
   * @return string without any tags wrapping it
   */
  public static String removeTags(String rawData) {
    return rawData.replaceAll("\\<[^>]+>\\>", "");
  }

  public static MacroRepresentation getMacroRepresentation(NodeList macros, StructuredCategory category, LSSerializer serializer, XhtmlContent xhtmlContent) {
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      String nodeName = node.getAttributes().getNamedItem("ac:name").getNodeValue();
      if (nodeName.equals("idea-structured-field") || nodeName.equals("Blueprint Id Storage")) {
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

  public static String DEFAULT_SCHEMA = "{\n" +
    "  \"title\": \"A fedex Idea or puzzle\",\n" +
    "  \"description\": \"Something interesting that could be worked on either in downtime or a fedex day\",\n" +
    "  \"type\": \"object\",\n" +
    "  \"required\": [\n" +
    "    \"ideaTitle\"\n" +
    "  ],\n" +
    "  \"properties\": {\n" +
    "    \"ideaTitle\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea Title (or how it should be know)\",\n" +
    "      \"default\": \"Other things\"\n" +
    "    },\n" +
    "    \"description\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Description\"\n" +
    "    },\n" +
    "    \"owner\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea owner\"\n" +
    "    },\n" +
    "    \"status\":{\n" +
    "          \"type\": \"string\",\n" +
    "          \"enum\": [\n" +
    "            \"new\",\n" +
    "            \"inProgress\",\n" +
    "            \"completed\",\n" +
    "            \"abandoned\"\n" +
    "          ],\"enumNames\": [\"New\", \"In Progress\", \"Completed\", \"Abandoned\"],\n" +
    "          \"default\": \"New\"\n" +
    "        \n" +
    "    },\n" +
    "        \"team\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The team\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "       \"technologies\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The tech\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "           \"links\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to resources for this idea\"\n" +
    "    },\n" +
    "           \"tickets\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to issues or tickets that track this\"\n" +
    "    },\n" +
    "           \"talks\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Presentations on the idea\"\n" +
    "    }\n" +
    "  }\n" +
    "}";


  /**
   * Query for Confluence page labels
   *
   * @param labels List of labels
   * @return Confluence searchable query for labels
   */
  private static BooleanQuery getLabelQuery(Set<String> labels) {
    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    labels.forEach(label -> booleanQueryFactory.addShould(new LabelQuery(label)));

    return booleanQueryFactory.toBooleanQuery();
  }


  /**
   * Creates a confluence searchable query to find pages of a certain type
   *
   * @param labels   list of page labels
   * @param spaceKey location of page search in confluence
   * @return Confluence searchable query
   */
  private static BooleanQuery createSearchableQuery(Set<String> labels, String spaceKey) {
    BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
    booleanQueryFactory.addMust(getLabelQuery(labels));
    booleanQueryFactory.addMust(new ContentTypeQuery(ContentTypeEnum.PAGE));
    booleanQueryFactory.addMust(new InSpaceQuery(spaceKey));

    return booleanQueryFactory.toBooleanQuery();
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
  private static Document parseXML(String xml)
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
  private static MacroRepresentation getMacroFromList(
    NodeList macros, StructuredCategory category, LSSerializer serializer) {
    return getMacroRepresentation(macros, category, serializer, xhtmlContent);
  }


  public static List<IdeaContainer> getRows(Set<String> labels, String spaceKey, SearchManager searchManager, SettingsManager settingsManager){
    SearchFilter searchFilter =
      ContentPermissionsSearchFilter.getInstance()
        .and(SpacePermissionsSearchFilter.getInstance());

    ContentSearch search =
      new ContentSearch(
        createSearchableQuery(labels, spaceKey),
        ModifiedSort.DESCENDING,
        searchFilter,
        new SubsetResultFilter(100));

    List<IdeaContainer> rows = Collections.emptyList();

    try {
      List<Searchable> maybePages =
        searchManager.searchEntities(search, EntityVersionPolicy.LATEST_VERSION);

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

    return rows;
  }

}
