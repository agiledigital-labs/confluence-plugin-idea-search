//package au.com.agiledigital.idea_search.listener;
//
//import au.com.agiledigital.idea_search.events.FedexIdeaCreateEvent;
//import au.com.agiledigital.idea_search.model.FedexIdea;
//import com.atlassian.confluence.xwork.FlashScope;
//import com.atlassian.event.api.EventListener;
//import org.springframework.stereotype.Component;
//import com.atlassian.event.api.EventPublisher;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
//
//@Component
//public class FedexIdeaDiscoveryEventListener {
//    private final EventPublisher eventPublisher;
//
//    @Autowired
//    public FedexIdeaDiscoveryEventListener(EventPublisher eventPublisher){ this.eventPublisher = eventPublisher;}
//
//    @PostConstruct
//    public final void setup() {this.eventPublisher.register((this));}
//
//    @PreDestroy
//    public final void teardown() {this.eventPublisher.unregister(this);}
//
//    @EventListener
//    public void fedexIdeaCreated(FedexIdeaCreateEvent event) {
//        FlashScope.put("create-fedex-idea", true);
//    }
//
//}
