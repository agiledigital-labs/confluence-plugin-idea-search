package au.com.agiledigital.idea_search.dao;

import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.service.DefaultFedexIdeaService;
import com.atlassian.activeobjects.external.ActiveObjects;
import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.DBParam;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.java.ao.Query;


/**
 * Fedex Idea Dao access the data stored with Atlassian active objects.
 * <p>
 * Used to create and find Fedex ideas stored in the database using active objects.
 */
@Component
public class FedexIdeaDao {
    @ComponentImport
    private final ActiveObjects ao;
    private static final Class<AoFedexIdea> AO_FEDEX_IDEA_TYPE = AoFedexIdea.class;
    private static final Class<AoFedexTechnology> AO_FEDEX_TECHNOLOGY_TYPE = AoFedexTechnology.class;
    @ComponentImport
    private final UserAccessor userAccessor;

    private static final Logger log = LoggerFactory.getLogger(DefaultFedexIdeaService.class);


    @Autowired
    public FedexIdeaDao(ActiveObjects ao, UserAccessor userAccessor) {
        this.ao = ao;
        this.userAccessor = userAccessor;
    }

    public FedexIdea create(FedexIdea fedexIdea) {
        AoFedexIdea aoFedexIdea = this.ao.create(AO_FEDEX_IDEA_TYPE, new DBParam[0]);
        List<AoFedexTechnology> aoTechList = new ArrayList<>();


        fedexIdea.getTechnologies().forEach(tech ->
                aoTechList.add(this.prepareAOFedexTechnology(tech.getTechnology()))
        );

        this.prepareAOFedexIdea(aoFedexIdea, fedexIdea, aoTechList);

        aoFedexIdea.save();

        aoTechList.forEach(li -> {
            li.setIdea(aoFedexIdea);
            li.save();
        });

        return this.asFedexIdea(aoFedexIdea);
    }

    public List<FedexIdea> findAll() {
        AoFedexIdea[] aoFedexIdeas = this.ao.find(AO_FEDEX_IDEA_TYPE);
        return this.asListFedexIdea(aoFedexIdeas);
    }

    public List<FedexTechnology> findAllTech() {
        AoFedexTechnology[] aoFedexTechnologies = (AoFedexTechnology[]) this.ao.find(AO_FEDEX_TECHNOLOGY_TYPE, Query.select("TECHNOLOGY").distinct());
        return this.asListFedexTechnology(aoFedexTechnologies);
    }

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


    private String getUserKey(String userName) {
        if (userName == null) {
            return null;
        } else {
            ConfluenceUser user = this.userAccessor.getUserByName(userName);
            return user == null ? null : user.getKey().getStringValue();
        }
    }

    private AoFedexTechnology prepareAOFedexTechnology(String technology) {
        AoFedexTechnology aoFedexTechnology = this.ao.create(AoFedexTechnology.class, new DBParam[0]);
        aoFedexTechnology.setTechnology(technology);
        return aoFedexTechnology;
    }

    private void prepareAOFedexIdea(AoFedexIdea aoFedexIdea, FedexIdea fedexIdea, List<AoFedexTechnology> aoTechList) {
        aoFedexIdea.setContentId(fedexIdea.getContentId());
        aoFedexIdea.setCreatorUserKey(this.getUserKey(fedexIdea.getCreator()));
        aoFedexIdea.setOwner(fedexIdea.getOwner());
        aoFedexIdea.setStatus(fedexIdea.getStatus());
        aoFedexIdea.setDescription(fedexIdea.getDescription());
    }

    private String getUsername(String userKey) {
        if (userKey == null) {
            return null;
        } else {
            ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
            return user == null ? null : user.getLowerName();
        }
    }

    private FedexIdea asFedexIdea(AoFedexIdea ao) {
        return ao == null ? null : (new au.com.agiledigital.idea_search.model.FedexIdea.Builder())
                .withGlobalId(ao.getGlobalId())
                .withOwner(ao.getOwner())
                .withContentId(ao.getContentId())
                .withCreator(this.getUsername(ao.getCreatorUserKey()))
                .withDescription(ao.getDescription())
                .withStatus(ao.getStatus())
                .build();
    }

    private FedexTechnology asFedexTechnology(AoFedexTechnology aoT) {
        return aoT == null ? null : (new au.com.agiledigital.idea_search.model.FedexTechnology.Builder())
                .withGlobalId(aoT.getGlobalId())
                .withTechnology(aoT.getTechnology())
                .build();
    }

    public List<String> techDaoList(){
        Query query = Query.select("TECHNOLOGY").order("TECHNOLOGY ASC");
        AoFedexTechnology [] aoFedexTechnologies = this.ao.find(AO_FEDEX_TECHNOLOGY_TYPE, query);

        List<String> technologies = Arrays.stream(aoFedexTechnologies).map(t -> t.getTechnology()).collect(Collectors.toList());
        return technologies;
    }
}
