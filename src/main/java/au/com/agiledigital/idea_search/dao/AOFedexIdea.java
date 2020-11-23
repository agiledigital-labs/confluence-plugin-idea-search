package au.com.agiledigital.idea_search.dao;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.PrimaryKey;

/**
 * DAO interface exported to osgi
 *
 * Used as an active object
 *
 */
public interface AOFedexIdea extends RawEntity<Long>  {
    @AutoIncrement
    @NotNull
    @PrimaryKey
    long getGlobalId();

    String getOwner();

    void setOwner(String owner);

    @Indexed
    long getContentId();

    void setContentId(long contentId);

    @StringLength(-1)
    String getTechnology();

    void setTechnology(String technology);

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
