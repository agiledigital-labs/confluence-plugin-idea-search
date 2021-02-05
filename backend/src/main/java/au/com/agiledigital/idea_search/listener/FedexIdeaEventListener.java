package au.com.agiledigital.idea_search.listener;

//import au.com.agiledigital.idea_search.macros.DataMacroRepresentation;
import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
  import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
  import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.actions.IndexPageManager;
  import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
  import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
  import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
  import java.util.List;

  import static au.com.agiledigital.idea_search.helpers.Utilities.getMacroRepresentation;

/**
 * Listens to confluence events Connects to event publisher, and sends filtered events to the idea
 * service
 */
@Named
public class FedexIdeaEventListener implements InitializingBean, DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(FedexIdeaEventListener.class);

  @ConfluenceImport
  private final EventPublisher eventPublisher;

  private final DefaultFedexIdeaService fedexIdeaService;
  private final XhtmlContent xhtmlContent;

  @ConfluenceImport
  private final IndexPageManager indexPageManager;

  private final DocumentBuilderFactory documentBuilderFactory =
    DocumentBuilderFactory.newInstance();
  private static final ModuleCompleteKey FEDEX_IDEA_BLUEPRINT_KEY =
    new ModuleCompleteKey("au.com.agiledigital.idea_search", "idea-blueprint");
  private static final ModuleCompleteKey FEDEX_IDEA_BLUEPRINT_KEY_V2 =
    new ModuleCompleteKey("au.com.agiledigital.idea_search", "idea-blueprint-V2");
  private static final String FEDEX_IDEA_BLUEPRINT_LABEL = "fedex-ideas";
  private static final String FEDEX_IDEA_BLUEPRINT_LABEL_V2 = "fedex-ideas-v2";
  private final ContentBlueprint contentBlueprint;

  /**
   * Construct with connection to the event publisher and FedexIdea service.
   *
   * @param eventPublisher   confluence event publisher
   * @param fedexIdeaService fedex Idea service
   * @param xhtmlContent     used to parse the page content
   * @param indexPageManager class for the index page
   */
  @Inject
  public FedexIdeaEventListener(
    EventPublisher eventPublisher,
    DefaultFedexIdeaService fedexIdeaService,
    @ComponentImport XhtmlContent xhtmlContent,
    IndexPageManager indexPageManager) {
    this.eventPublisher = eventPublisher;
    this.fedexIdeaService = fedexIdeaService;
    this.xhtmlContent = xhtmlContent;
    this.indexPageManager = indexPageManager;
    this.contentBlueprint = new ContentBlueprint();
    this.contentBlueprint.setModuleCompleteKey(FEDEX_IDEA_BLUEPRINT_KEY.toString());
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
   *
   * @param xml string of readin XML content
   * @return Object containing the structure of the XML which has functionality for navigating the
   * dom
   * @throws ParserConfigurationException exception
   * @throws IOException                  exception
   * @throws SAXException                 exception
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


//  /**
//   * Listen for page update events on pages with the correct label, updates the data store with the
//   * new idea
//   *
//   * @param event produced when a page is updated
//   */
//  @EventListener
//  public void pageUpdated(PageUpdateEvent event) throws IOException, SAXException, ParserConfigurationException {
//    Document bodyParsed =  event.getPage().getBodyContents();
////      .stream().map(body -> body.getContent())
////      .collect(Collectors.toList());
//    NodeList macros = bodyParsed.getElementsByTagName("ac:structured-macro");
//
//
//    new DataMacroRepresentation(macros);
//
//  }
//
//  /**
//   * Listen for page creations events on pages with the correct label, updates the data store with
//   * the new idea
//   * <p>
//   * If the title of the page is not unique, the blueprint create event is not used, the page create
//   * event is.
//   *
//   * @param event produced when a page is updated
//   */
//  @EventListener
//  public void pageCreated(PageCreateEvent event) {
//
//  }


  /**
   * Listen for page creations events on pages with the correct label, updates the data store with
   * the new idea
   * <p>
   * If the title of the page is not unique, the blueprint create event is not used, the page create
   * event is.
   *
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageCreated(PageCreateEvent event) throws IOException, SAXException, ParserConfigurationException {
    event.getUpdateTrigger();
//    createOrUpdateTechnology(event.getContent(), event.getPage());
    this.fedexIdeaService.createIdea(getFedexIdea(event.getPage()));

  }

  /**
   * Puts the newly created page as a child of index page
   *
   * @param page the new page
   */
  private void makeChildOfIndex(Page page) {
    Page indexPage = this.indexPageManager.findIndexPage(this.contentBlueprint, page.getSpace());

    indexPage.addChild(page);
  }

  @EventListener
  public void macroUpdateEvent(PageUpdateEvent pageUpdateEvent){
//
//    pageUpdateEvent.getPage().getBodyContent().
//
//    pageUpdateEvent.getPage().getBodyContents().stream().map(content -> content);
  }

//
//  /**
//   * Creates or updates a fedex technology page
//   *
//   * @param content the content of the event
//   * @param page    the new page
//   */
//  private void createOrUpdateTechnology(ContentEntityObject content, Page page) {
//    if (
//      content.getLabels().toString().contains(FEDEX_IDEA_BLUEPRINT_LABEL)
//    ) {
//      try {
//        makeChildOfIndex(page);
////        FedexIdea idea = getFedexIdea(page);
////        this.fedexIdeaService.updateIdea(idea, page.getId());
//      } catch (ParserConfigurationException | IOException | SAXException e) {
//        log.debug(e.getMessage());
//      }
//    }
//  }

  /**
   * Pares structured data from page and return a fedex idea.
   *
   * @param page content with structured data
   * @return FedexIdea model object
   * @throws ParserConfigurationException exception
   * @throws IOException                  exception
   * @throws SAXException                 exception
   */
  @Deprecated
  private FedexIdea getFedexIdea(AbstractPage page)
    throws ParserConfigurationException, IOException, SAXException {

    // Splits the comma separated string into a list and replaces all html tags
    // TODO: Get the actual technology list
    // List<String> tech = Arrays.stream(row.getTechnologies().getValue().split("\\s*,\\s*"))
    //   .map(Utilities::removeTags).collect(
    //     Collectors.toList());

    List<String> tech = new ArrayList<>();
    tech.add("python");
    tech.add("java");


    List<FedexTechnology> techList = new ArrayList<>();

    tech.forEach(t -> techList.add(new FedexTechnology.Builder().withTechnology(t).build()));

    return new FedexIdea.Builder()
      .withTitle("Title from builder")
      .withTechnologies(techList)
//      .withContentId(page.getId()).withSchemaId(4)
      .withCreator(page.getCreator().getName())
      .withDescription("Demo description from builder after reload")
      .withStatus("Boss")
      .withOwner("admin")
      .build();
  }
}
