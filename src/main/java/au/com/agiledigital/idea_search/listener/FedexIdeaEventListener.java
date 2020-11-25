package au.com.agiledigital.idea_search.listener;

import static au.com.agiledigital.idea_search.helpers.PageHelper.wrapBody;

import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import au.com.agiledigital.idea_search.macros.transport.IdeaContainer;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

@Named
public class FedexIdeaEventListener
  implements InitializingBean, DisposableBean {

  @ConfluenceImport
  private final EventPublisher eventPublisher;

  private final DefaultFedexIdeaService fedexIdeaService;
  private final XhtmlContent xhtmlContent;

  private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private static final ModuleCompleteKey MY_BLUEPRINT_KEY = new ModuleCompleteKey(
    "au.com.agiledigital.idea_search",
    "idea-blueprint"
  );
  private static final String MY_BLUEPRINT_LABEL = "fedex-ideas";

  @Inject
  public FedexIdeaEventListener(
    EventPublisher eventPublisher,
    DefaultFedexIdeaService fedexIdeaService,
    @ComponentImport XhtmlContent xhtmlContent
  ) {
    this.eventPublisher = eventPublisher;
    this.fedexIdeaService = fedexIdeaService;
    this.xhtmlContent = xhtmlContent;
  }

  @Override
  public void destroy() {
    eventPublisher.unregister(this);
  }

  @Override
  public void afterPropertiesSet() {
    eventPublisher.register(this);
  }

  /**
   * Parses XML to Java Dom objects
   * @param xml string of readin XML content
   * @return Object containing the structure of the XML which has functionality for navigating the
   * dom
   * @throws ParserConfigurationException exception
   * @throws IOException exception
   * @throws SAXException exception
   */
  private Document parseXML(String xml)
    throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

    return builder.parse(new ByteArrayInputStream(xml.getBytes()));
  }

  /**
   * Finds a Structured Field macro with a category from a list of macros
   * @param macros     Structured Field macro list
   * @param category   The needle for the search
   * @param serializer XML serializer
   * @return Representation of a macro from Confluence Storage format
   */
  private MacroRepresentation getMacroFromList(
    NodeList macros,
    StructuredCategory category,
    LSSerializer serializer
  ) {
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      String nodeName = node
        .getAttributes()
        .getNamedItem("ac:name")
        .getNodeValue();
      if (
        nodeName.equals("Idea Structured Field") ||
        nodeName.equals("Blueprint Id Storage")
      ) {
        Node child = node.getFirstChild();
        do {
          if (
            child instanceof Element &&
            child.getNodeName().equals("ac:parameter") &&
            child.getTextContent().equals(category.getKey())
          ) {
            return new MacroRepresentation(
              node,
              category,
              serializer,
              xhtmlContent
            );
          }
        } while ((child = child.getNextSibling()) != null);
      }
    }

    return null;
  }

  /**
   * Listen for pages created from blueprints.
   * @param event created when a pages is created from a blueprint
   */
  @EventListener
  public void onBlueprintCreateEvent(BlueprintPageCreateEvent event) {
    String moduleCompleteKey = event.getBlueprint().getModuleCompleteKey();

    String thing = MY_BLUEPRINT_KEY.getCompleteKey();

    if (thing.equals(moduleCompleteKey)) {
      try {
        FedexIdea idea = getFedexIdea(event.getPage());
        this.fedexIdeaService.create(idea);
      } catch (ParserConfigurationException | IOException | SAXException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Listen for page update events on pages with the correct label,
   * updates the data store with the new idea
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageUpdated(PageUpdateEvent event) {
    if (
      event.getContent().getLabels().toString().contains(MY_BLUEPRINT_LABEL)
    ) {
      try {
        FedexIdea idea = getFedexIdea(event.getPage());
        this.fedexIdeaService.update(idea, event.getPage().getId());
      } catch (ParserConfigurationException | IOException | SAXException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Pares structured data from page and return a fedex idea.
   * @param page content with structured data
   * @return FedexIdea model object
   * @throws ParserConfigurationException exception
   * @throws IOException exception
   * @throws SAXException exception
   */
  private FedexIdea getFedexIdea(AbstractPage page)
    throws ParserConfigurationException, IOException, SAXException {
    Document bodyParsed = parseXML(wrapBody(page.getBodyAsString()));
    NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");
    DOMImplementationLS ls = (DOMImplementationLS) bodyParsed.getImplementation();
    LSSerializer serializer = ls.createLSSerializer();
    IdeaContainer row = new IdeaContainer();
    row.title = page.getDisplayTitle();
    Arrays
      .asList(StructuredCategory.values())
      .forEach(
        category ->
          row.setMacroRepresentations(
            category,
            getMacroFromList(macros, category, serializer)
          )
      );

    List<String> tech = Arrays.asList(
      row.getTechnologies().getValue().split("\\s*,\\s*")
    );

    List<FedexTechnology> techList = new ArrayList<>();

    tech.forEach(
      t -> techList.add(new FedexTechnology.Builder().withTechnology(t).build())
    );

    return new FedexIdea.Builder()
      .withTechnologies(techList)
      .withContentId(page.getId())
      .withCreator(page.getCreator().getName())
      .withDescription(row.getDescription().getValue())
      .withStatus(row.getStatus().getValue())
      .withOwner(row.getOwner().getValue())
      .build();
  }
}
