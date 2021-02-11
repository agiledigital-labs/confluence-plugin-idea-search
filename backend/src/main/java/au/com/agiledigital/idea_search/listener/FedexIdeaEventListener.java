package au.com.agiledigital.idea_search.listener;

import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
  import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.actions.IndexPageManager;
  import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.setup.settings.SettingsManager;
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
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import static au.com.agiledigital.idea_search.helpers.Utilities.getPageData;

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
private SettingsManager settingsManager;

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
    SettingsManager settingsManager,
    DefaultFedexIdeaService fedexIdeaService,
    @ComponentImport XhtmlContent xhtmlContent,
    IndexPageManager indexPageManager) {
    this.eventPublisher = eventPublisher;
    this.fedexIdeaService = fedexIdeaService;
    this.xhtmlContent = xhtmlContent;
    this.indexPageManager = indexPageManager;
    this.contentBlueprint = new ContentBlueprint();
    this.contentBlueprint.setModuleCompleteKey(FEDEX_IDEA_BLUEPRINT_KEY.toString());
    this.settingsManager = settingsManager;

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
   * Listen for page update events on pages with the correct label, updates the data store with the
   * new idea
   *
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageUpdated(PageUpdateEvent event) throws IOException, SAXException, ParserConfigurationException {

    this.fedexIdeaService.updateIdea(getPageData(this.settingsManager, event.getPage()), event.getPage().getId());


  }


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
    this.fedexIdeaService.createIdea(getPageData(this.settingsManager, event.getPage()));


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



}
