package au.com.agiledigital.structured_form.listener;

import au.com.agiledigital.structured_form.service.DefaultFormDataService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import static au.com.agiledigital.structured_form.helpers.Utilities.formDataFromPage;

/**
 * Listens to confluence events Connects to event publisher, and sends filtered events to the data
 * service
 */
@Named
public class FormDataEventListener implements InitializingBean, DisposableBean {

  @ConfluenceImport
  private final EventPublisher eventPublisher;

  private final DefaultFormDataService defaultFormDataService;


  private static final ModuleCompleteKey FORM_DATA_BLUEPRINT_KEY =
    new ModuleCompleteKey("au.com.agiledigital.structured_form", "form-data-blueprint");
  private static final String FORM_DATA_BLUEPRINT_LABEL = "form-data";

  /**
   * Construct with connection to the event publisher and FormData service.
   *
   * @param eventPublisher   confluence event publisher
   * @param defaultFormDataService form data service
   */
  @Inject
  public FormDataEventListener(
    EventPublisher eventPublisher,
    DefaultFormDataService defaultFormDataService) {
    this.eventPublisher = eventPublisher;
    this.defaultFormDataService = defaultFormDataService;
    ContentBlueprint contentBlueprint = new ContentBlueprint();
    contentBlueprint.setModuleCompleteKey(FORM_DATA_BLUEPRINT_KEY.toString());
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
   * Listen for pages created from blueprints.
   *
   * @param event created when a pages is created from a blueprint
   */
  @EventListener
  public void onBlueprintCreateEvent(@Nonnull BlueprintPageCreateEvent event) {

    String moduleCompleteKey = event.getBlueprint().getModuleCompleteKey();

    String blueprintKey = FORM_DATA_BLUEPRINT_KEY.getCompleteKey();

    if (blueprintKey.equals(moduleCompleteKey)) {
      // Gets the blueprintId and sets it as the current one in ao database
      String blueprintId = String.valueOf(event.getBlueprint().getId());
      this.defaultFormDataService.setBlueprintId(blueprintId);
    }
  }

  /**
   * Listen for page creations events on pages with the correct label, updates the data store with
   * the new data
   *
   * If the title of the page is not unique, the blueprint create event is not used, the page create
   * event is.
   *
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageCreated(@Nonnull PageCreateEvent event) {
    pageEventHandler(event.getContent(), event.getPage());
  }

  /**
   * Listen for page deletion events on pages with the correct label, updates the data store with
   * the deleted tag
   *
   * If the title of the page is not unique, the blueprint create event is not used, the page create
   * event is.
   *
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageDelete(@Nonnull PageRemoveEvent event) {
    if (
      event.getContent().getLabels().contains(new Label(FORM_DATA_BLUEPRINT_LABEL))
    ) {
      this.defaultFormDataService.removeForm(formDataFromPage(event.getPage()), event.getPage().getId());
    }
  }

  /**
   * Listen for page update events on pages with the correct label, updates the data store with the
   * new data
   *
   * @param event produced when a page is updated
   */
  @EventListener
  public void pageUpdated(@Nonnull PageUpdateEvent event) {
    pageEventHandler(event.getContent(), event.getPage());
  }

  private void pageEventHandler(@Nonnull ContentEntityObject content, @Nonnull Page page) {
    if (
      content.getLabels().contains(new Label(FORM_DATA_BLUEPRINT_LABEL))
    ) {
      this.defaultFormDataService.upsertFormData(formDataFromPage(page), page.getId());
    }
  }
}
