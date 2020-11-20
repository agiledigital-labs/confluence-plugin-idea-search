package au.com.agiledigital.idea_search.events;

import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.user.User;

public abstract class AbstractFedexIdeaCreateEvent extends ConfluenceEvent implements UserDriven {
    protected final User user;
    protected final FedexIdea fedexIdea;

    public AbstractFedexIdeaCreateEvent(Object src, User user, FedexIdea fedexIdea) {
        super(src);
        this.user = user;
        this.fedexIdea = fedexIdea;
    }

    public FedexIdea getFedexIdea() {return this.fedexIdea;}

    public User getOriginatingUser() {
        return this.user;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{user=" + this.user +", fedexIdea=" + this.fedexIdea + "}";
    }
}
