package au.com.agiledigital.idea_search.listener;

import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
  import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.actions.IndexPageManager;
  import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import javax.inject.Named;

import static au.com.agiledigital.idea_search.helpers.Utilities.getPageData;

/**
 * Listens to confluence events Connects to event publisher, and sends filtered events to the idea
 * service
 */
@Named
public class FedexIdeaEventListener implements InitializingBean, DisposableBean {

  @ConfluenceImport
  private final EventPublisher eventPublisher;

  private final DefaultFedexIdeaService fedexIdeaService;

  @ConfluenceImport
  private final IndexPageManager indexPageManager;

  private static final ModuleCompleteKey FEDEX_IDEA_BLUEPRINT_KEY =
    new ModuleCompleteKey("au.com.agiledigital.idea_search", "idea-blueprint");
  private static final String FEDEX_IDEA_BLUEPRINT_LABEL = "fedex-ideas";
  private final ContentBlueprint contentBlueprint;
private SettingsManager settingsManager;

  /**
   * Construct with connection to the event publisher and FedexIdea service.
   *
   * @param eventPublisher   confluence event publisher
   * @param fedexIdeaService fedex Idea service
   * @param indexPageManager class for the index page
   */
  @Inject
  public FedexIdeaEventListener(
    EventPublisher eventPublisher,
    SettingsManager settingsManager,
    DefaultFedexIdeaService fedexIdeaService,
    IndexPageManager indexPageManager) {
    this.eventPublisher = eventPublisher;
    this.fedexIdeaService = fedexIdeaService;
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
  public void pageUpdated(PageUpdateEvent event)  {
    if (
      event.getContent().getLabels().toString().contains(FEDEX_IDEA_BLUEPRINT_LABEL)
    ) {
        makeChildOfIndex(event.getPage());

    this.fedexIdeaService.updateIdea(getPageData(this.settingsManager, event.getPage()), event.getPage().getId());
      }



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
  public void pageCreated(PageCreateEvent event)  {
    if (
      event.getContent().getLabels().toString().contains(FEDEX_IDEA_BLUEPRINT_LABEL)
    ) {

        makeChildOfIndex(event.getPage());

        this.fedexIdeaService.updateIdea(getPageData(this.settingsManager, event.getPage()), event.getPage().getId());
      }



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
