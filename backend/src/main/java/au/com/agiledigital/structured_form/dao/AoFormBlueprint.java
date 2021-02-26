package au.com.agiledigital.structured_form.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

import javax.annotation.Nonnull;

/**
 * Idea blueprint table definition, used to store blueprint id
 */
public interface AoFormBlueprint extends RawEntity<Long> {

  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();

  @Nonnull
  @StringLength(36)
  String getBlueprintId();

  void setBlueprintId(String blueprintId);
}
