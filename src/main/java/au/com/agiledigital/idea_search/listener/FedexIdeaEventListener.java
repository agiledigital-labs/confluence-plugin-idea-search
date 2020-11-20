package au.com.agiledigital.idea_search.listener;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.events.FedexIdeaCreateEvent;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class FedexIdeaEventListener implements InitializingBean, DisposableBean {
    @ConfluenceImport
    private EventPublisher eventPublisher;
    private DefaultFedexIdeaService fedexIdeaService;

    private static final Logger log = LoggerFactory.getLogger(FedexIdeaEventListener.class);
    private static final ModuleCompleteKey MY_BLUEPRINT_KEY = new ModuleCompleteKey("au.com.agiledigital.idea_search", "my-blueprint");



    @Inject
    public FedexIdeaEventListener(EventPublisher eventPublisher, DefaultFedexIdeaService fedexIdeaService) {
        this.eventPublisher = eventPublisher;
        this.fedexIdeaService = fedexIdeaService;
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    @EventListener
    public void onBlueprintCreateEvent(BlueprintPageCreateEvent event) {
        String moduleCompleteKey = event.getBlueprint().getModuleCompleteKey();

        String thing =MY_BLUEPRINT_KEY.getCompleteKey();

        if(thing.equals(moduleCompleteKey)) {

            String thing1 = event.getContent().getBodyAsString();
            event.getContent().getBodyContents().forEach(c -> {
                String temp = c.getContent().getBodyAsStringWithoutMarkup();
                log.warn(" \n\n\n\n\n\n1" +temp);
            });
            log.warn(" \n\n\n\n\n\n2" +event.getPage().getBodyAsString());


            log.warn("This is thing:" + thing1 + thing + "" + moduleCompleteKey + "WARN: Created a blueprint. \n\n\n\n\n\n -----------------------------------------------------------------------------------------------------------------------");

            FedexIdea idea = new FedexIdea.Builder().withTechnology("This,is,A,test").build();
            this.fedexIdeaService.create(idea);
        }
    }

}


//@Component
//public class FedexIdeaEventListener  implements EventListener {
//    @ConfluenceImport
//    private EventPublisher eventPublisher;
//
//    @Inject
//    public void FedexIdeaEventListener(EventPublisher eventPublisher) {
//        eventPublisher.register(this);  //just for example
//    }
//
//    // Unregister the listener if the plugin is uninstalled or disabled.
//    public void destroy() throws Exception {
//        eventPublisher.unregister(this);
//    }
//
//    @Override
//    public String scope() {
//        return null;
//    }
//
//    @Override
//    public int order() {
//        return 0;
//    }
//
//    @Override
//    public Class<? extends Annotation> annotationType() {
//        return null;
//    }


//    private final EventPublisher eventPublisher;
//    private final FedexIdeaService fedexIdeaService;
//
//
//    @Inject
//    public  FedexIdeaEventListener(EventPublisher eventPublisher, FedexIdeaService fedexIdeaService){
//        this.eventPublisher = eventPublisher;
//        this.fedexIdeaService = fedexIdeaService;
//    }
//
//
//    public void destroy() throws Exception {
//        this.eventPublisher.unregister(this);
//    }
//
//    @Override
//    public String scope() {
//        return null;
//    }
//
//    @Override
//    public int order() {
//        return 0;
//    }
//
//    @Override
//    public Class<? extends Annotation> annotationType() {
//        return null;
//    }
//}
