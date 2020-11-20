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

    long getId();

    void setId(long var1);

    @Indexed
    long getContentId();

    void setContentId(long var1);

    @StringLength(-1)
    String getTechnology();

    void setTechnology(String var1);

    @Indexed
    String getCreatorUserKey();

    void setCreatorUserKey(String var1);
}
