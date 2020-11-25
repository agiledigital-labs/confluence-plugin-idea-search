package au.com.agiledigital.idea_search.dao;

import au.com.agiledigital.idea_search.model.FedexTechnology;
import com.atlassian.activeobjects.external.ActiveObjects;
import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.DBParam;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import net.java.ao.RawEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.java.ao.Query;


/**
 * Fedex Idea Dao access the data store
 *
 */
@Component
public class FedexIdeaDao {
    @ComponentImport
    private final ActiveObjects ao;
    private static final Class<AoFedexIdea> AO_FEDEX_IDEA_TYPE = AoFedexIdea.class;
    private static final Class<AoFedexTechnology> AO_FEDEX_TECHNOLOGY_TYPE = AoFedexTechnology.class;
    @ComponentImport
    private final UserAccessor userAccessor;


    @Autowired
    public FedexIdeaDao(ActiveObjects ao, UserAccessor userAccessor) {
        this.ao = ao;
        this.userAccessor = userAccessor;
    }

    /**
     * Create new entry/entries that represent the inputted FedexIdea object
     *
     * @param fedexIdea FedexIdea model object
     * @return FedexIdea object created in data store
     */

    public FedexIdea create(FedexIdea fedexIdea) {
        AoFedexIdea aoFedexIdea = this.ao.create(AO_FEDEX_IDEA_TYPE, new DBParam[0]);
        List<AoFedexTechnology> aoTechList = new ArrayList<>();

        fedexIdea.getTechnologies().forEach(tech ->
                aoTechList.add(this.prepareAOFedexTechnology(tech.getTechnology()))
        );

        this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

        aoFedexIdea.save();

        aoTechList.forEach(li -> {
            li.setIdea(aoFedexIdea);
            li.save();
        });


        return this.asFedexIdea(aoFedexIdea);
    }

    /**
     * Update a FedexIdea by the page content id
     *
     * @param fedexIdea New FedexIdea for data store
     * @param contentId of the page containing the idea
     * @return FedexIdea saved to the data store
     */
    public FedexIdea updateByContentId(FedexIdea fedexIdea, long contentId) {
        AoFedexIdea aoFedexIdea = this.ao.find(AO_FEDEX_IDEA_TYPE, Query.select().where("CONTENT_ID = ?", contentId))[0];
//        If the title of the page already exists the create event fails and a page update event is fired on a successful save,
//        if this happens the aoFedexIdea will not exist in the data store and will need to be created.
        if(aoFedexIdea == null) {
            aoFedexIdea = this.ao.create(AO_FEDEX_IDEA_TYPE, new DBParam[0]);
        }

        List<AoFedexTechnology> aoFedexTechnology = Arrays.asList(aoFedexIdea.getTechnology());
        aoFedexTechnology.forEach(tech ->
                this.ao.delete(new RawEntity[]{tech})
        );


        List<AoFedexTechnology> aoTechList = new ArrayList<>();


        fedexIdea.getTechnologies().forEach(tech ->
                aoTechList.add(this.prepareAOFedexTechnology(tech.getTechnology()))
        );

        this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

        aoFedexIdea.save();

        final AoFedexIdea idea = aoFedexIdea;

        aoTechList.forEach(li -> {
            li.setIdea(idea);
            li.save();
        });

        return this.asFedexIdea(idea);
    }

    /**
     * List all fedex idea in the data store
     * @return List<FedexIdea>
     */
    public List<FedexIdea> findAll() {
        AoFedexIdea[] aoFedexIdeas = this.ao.find(AO_FEDEX_IDEA_TYPE);
        return this.asListFedexIdea(aoFedexIdeas);
    }

    /**
     * List all fedex idea technology in the data store
     * @return List<FedexTechnology>
     */
    public List<FedexTechnology> findAllTech() {
        AoFedexTechnology[] aoFedexTechnologies = (AoFedexTechnology[]) this.ao.find(AO_FEDEX_TECHNOLOGY_TYPE, Query.select());
        return this.asListFedexTechnology(aoFedexTechnologies);
    }

    /**
     * Convert array of active objects to a list of model objects
     * @param aoFedexIdeas AoFedexIdea[]
     * @return List<FedexIdea>
     */
    private List<FedexIdea> asListFedexIdea(AoFedexIdea[] aoFedexIdeas) {
        List<FedexIdea> ideas = Lists.newArrayList();
        AoFedexIdea[] fedexIdeas = aoFedexIdeas;
        int numberIdeas = aoFedexIdeas.length;

        for (int counter = 0; counter < numberIdeas; ++counter) {
            AoFedexIdea aoFedexIdea = fedexIdeas[counter];
            ideas.add(this.asFedexIdea(aoFedexIdea));
        }

        return ideas;
    }

    /**
     * Convert array of active objects to a list of model objects
     * @param aoFedexTechnologies AoFedexTechnology[]
     * @return List<FedexTechnology>
     */
    private List<FedexTechnology> asListFedexTechnology(AoFedexTechnology[] aoFedexTechnologies) {
        List<FedexTechnology> tech = Lists.newArrayList();
        AoFedexTechnology[] fedexTechnologies = aoFedexTechnologies;
        int numberIdeas = aoFedexTechnologies.length;

        for (int counter = 0; counter < numberIdeas; ++counter) {
            AoFedexTechnology aoFedexTechnology = fedexTechnologies[counter];
            tech.add(this.asFedexTechnology(aoFedexTechnology));
        }

        return tech;
    }

    /**
     * Convert a user name string into a user key ID
     *
     * @param userName taking the confluence action
     * @return string of the user key ID
     */
    private String getUserKey(String userName) {
        if (userName == null) {
            return null;
        } else {
            ConfluenceUser user = this.userAccessor.getUserByName(userName);
            return user == null ? null : user.getKey().getStringValue();
        }
    }

    /**
     * Convert a user key ID to the users name
     *
     * @param userKey string of the user key id
     * @return userName string
     */
    private String getUsername(String userKey) {
        if (userKey == null) {
            return null;
        } else {
            ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
            return user == null ? null : user.getLowerName();
        }
    }

    /**
     * Prepare technology string for saving as an an active object
     * @param technology string
     * @return AoFedexTechnology object to be saved to the data store
     */
    private AoFedexTechnology prepareAOFedexTechnology(String technology) {
        AoFedexTechnology aoFedexTechnology = this.ao.create(AoFedexTechnology.class, new DBParam[0]);
        aoFedexTechnology.setTechnology(technology);
        return aoFedexTechnology;
    }

    /**
     * Prepare fedex active object with the data from a fedex idea
     * @param aoFedexIdea active object
     * @param fedexIdea with data to be added to the active object
     */
    private void prepareAOFedexIdea(AoFedexIdea aoFedexIdea, FedexIdea fedexIdea) {
        aoFedexIdea.setContentId(fedexIdea.getContentId());
        aoFedexIdea.setCreatorUserKey(this.getUserKey(fedexIdea.getCreator()));
        aoFedexIdea.setOwner(fedexIdea.getOwner());
        aoFedexIdea.setStatus(fedexIdea.getStatus());
        aoFedexIdea.setDescription(fedexIdea.getDescription());
    }

    /**
     * Convert fedex idea active object to a fedex idea model object
     * @param aoFedexIdea active object to be converted
     * @return FedexIdea object
     */
    private FedexIdea asFedexIdea(AoFedexIdea aoFedexIdea) {
        return aoFedexIdea == null ? null : (new au.com.agiledigital.idea_search.model.FedexIdea.Builder())
                .withGlobalId(aoFedexIdea.getGlobalId())
                .withOwner(aoFedexIdea.getOwner())
                .withContentId(aoFedexIdea.getContentId())
                .withCreator(this.getUsername(aoFedexIdea.getCreatorUserKey()))
                .withDescription(aoFedexIdea.getDescription())
                .withStatus(aoFedexIdea.getStatus())
                .build();
    }

    /**
     * Convert fedex technology active object to a fedex technology model object
     * @param aoFedexTechnology active object to be converted
     * @return FedexTechnology object
     */
    private FedexTechnology asFedexTechnology(AoFedexTechnology aoFedexTechnology) {
        return aoFedexTechnology == null ? null : (new au.com.agiledigital.idea_search.model.FedexTechnology.Builder())
                .withGlobalId(aoFedexTechnology.getGlobalId())
                .withTechnology(aoFedexTechnology.getTechnology())
                .build();
    }

}
