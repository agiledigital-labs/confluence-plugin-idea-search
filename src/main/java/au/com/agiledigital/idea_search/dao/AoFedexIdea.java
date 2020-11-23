package au.com.agiledigital.idea_search.dao;
import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.*;

import java.util.List;

/**
 * DAO interface exported to osgi
 *
 * Used as an active object
 *
 */
public interface AoFedexIdea extends RawEntity<Long>  {
    @AutoIncrement
    @NotNull
    @PrimaryKey
    long getGlobalId();

    String getOwner();
    void setOwner(String owner);

    @Indexed
    long getContentId();
    void setContentId(long contentId);

    @OneToMany
    AoFedexTechnology getTechnology();

    @StringLength(-1)
    String getStatus();
    void  setStatus(String status);

    @StringLength(-1)
    String getDescription();
    void setDescription(String team);

    @Indexed
    String getCreatorUserKey();
    void setCreatorUserKey(String creatorUserKey);

}
