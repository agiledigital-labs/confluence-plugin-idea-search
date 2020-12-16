package au.com.agiledigital.idea_search.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

/** Idea blueprint table definition, used to store blueprint id */
public interface AoIdeaBlueprint extends RawEntity<Long> {
  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();

  @StringLength(36)
  String getBlueprintId();

  void setBlueprintId(String blueprintId);
}
