package au.com.agiledigital.idea_search.events;

import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.user.User;

@EventName("idea-search.idea.created")
public class FedexIdeaCreateEvent extends AbstractFedexIdeaCreateEvent{
    public FedexIdeaCreateEvent(Object src, User user, FedexIdea fedexIdea) {super (src, user, fedexIdea);}

    public String  getTechnology() { return this.fedexIdea.getTechnology();}


}
