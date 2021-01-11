package au.com.agiledigital.idea_search.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

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

  void setTechnology(String technology);

  void setIdea(AoFedexIdea aoFedexIdea);
}
