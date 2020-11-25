package au.com.agiledigital.idea_search.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.*;

/**
 * Technology table definition, related to AoFedexIdea
 */
public interface AoFedexTechnology extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey
    long getGlobalId();

    @StringLength(-1)
    String getTechnology();
    void setTechnology(String Technology);

    void setIdea(AoFedexIdea aoFedexIdea);
}
