package au.com.agiledigital.idea_search.events;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexTechnology;
import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.user.User;

import java.util.List;

@EventName("idea-search.idea.created")
public class FedexIdeaCreateEvent extends AbstractFedexIdeaCreateEvent{
    public FedexIdeaCreateEvent(Object src, User user, FedexIdea fedexIdea) {super (src, user, fedexIdea);}

    public List<FedexTechnology> getTechnologies() { return this.fedexIdea.getTechnologies();}


}
