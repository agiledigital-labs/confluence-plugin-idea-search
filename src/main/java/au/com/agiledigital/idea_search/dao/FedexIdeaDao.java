package au.com.agiledigital.idea_search.dao;

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

import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * Fedex Idea Dao access the data stored with Atlassian active objects.
 * <p>
 * Used to create and find Fedex ideas stored in the database using active objects.
 */
@Component
public class FedexIdeaDao {
    @ComponentImport
    private final ActiveObjects ao;
    private static final Class<AOFedexIdea> AO_FEDEX_IDEA_TYPE = AOFedexIdea.class;
    @ComponentImport
    private final UserAccessor userAccessor;

    private String[] ao_column_names = new String[]{"t.GLOBAL_ID", "t.OWNER", "t.CONTENT_ID", "t.TECHNOLOGY", "t.CREATOR", "t.TITLE", "t.STATUS", "t.DESCRIPTION"};
    private Map<String, String> tableFieldName;

    private static final Function<Long, String> SELECT_ID_GENERIC = (input) -> {
        return "union all select ?";
    };

    private static final Function<Long, String> SELECT_ID_HSQLDB = (input) -> {
        return "union all select ? from INFORMATION_SCHEMA.SYSTEM_USERS limit 1";
    };
    private static final Function<Long, String> SELECT_ID_ORACLE = (input) -> {
        return "union all select ? from DUAL";
    };

    @Autowired
    public FedexIdeaDao(ActiveObjects ao, UserAccessor userAccessor) {
        this.ao = ao;
        this.userAccessor = userAccessor;
    }

    public FedexIdea create(FedexIdea fedexIdea) {
        AOFedexIdea aoFedexIdea = (AOFedexIdea) this.ao.create(AOFedexIdea.class, new DBParam[0]);
        this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);


        aoFedexIdea.save();
        return this.asFedexIdea(aoFedexIdea);
    }

    public List<FedexIdea> findAll() {
        AOFedexIdea[] aoFedexIdeas = (AOFedexIdea[]) this.ao.find(AO_FEDEX_IDEA_TYPE);
        return this.asListFedexIdea(aoFedexIdeas);
    }

    private List<FedexIdea> asListFedexIdea(AOFedexIdea[] aoFedexIdeas) {
        List<FedexIdea> tasks = Lists.newArrayList();
        AOFedexIdea[] fedexIdeas = aoFedexIdeas;
        int numberIdeas = aoFedexIdeas.length;

        for (int counter = 0; counter < numberIdeas; ++counter) {
            AOFedexIdea aoInlineTask = fedexIdeas[counter];
            tasks.add(this.asFedexIdea(aoInlineTask));
        }

        return tasks;
    }

    private String getUserKey(String userName) {
        if (userName == null) {
            return null;
        } else {
            ConfluenceUser user = this.userAccessor.getUserByName(userName);
            return user == null ? null : user.getKey().getStringValue();
        }
    }

    private void prepareAOFedexIdea(AOFedexIdea aoFedexIdea, FedexIdea fedexIdea) {
        aoFedexIdea.setContentId(fedexIdea.getContentId());
        aoFedexIdea.setCreatorUserKey(this.getUserKey(fedexIdea.getCreator()));
        aoFedexIdea.setOwner(fedexIdea.getOwner());
        aoFedexIdea.setTechnology(fedexIdea.getTechnology());
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

    private FedexIdea asFedexIdea(AOFedexIdea ao) {
        return ao == null ? null : (new au.com.agiledigital.idea_search.model.FedexIdea.Builder())
                .withGlobalId(ao.getGlobalId())
                .withOwner(ao.getOwner())
                .withContentId(ao.getContentId())
                .withTechnology(ao.getTechnology())
                .withCreator(this.getUsername(ao.getCreatorUserKey()))
                .withDescription(ao.getDescription())
                .withStatus(ao.getStatus())
                .build();
    }
}
