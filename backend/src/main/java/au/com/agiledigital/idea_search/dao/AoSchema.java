package au.com.agiledigital.idea_search.dao;

import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.*;

/**
 * FedexIdea data store object,
 *
 * DAO interface used by active objects to create the data store tables
 *
 * OneToMany annotation creates PK/FK relation between the two tables
 *
 */
public interface AoSchema extends RawEntity<Long> {
  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();


  @StringLength(-1)
  String getDescription();

  void setDescription(String team);

  @StringLength(100)
  String getName();

  void setName(String name);

  Integer getVersion();

  void setVersion(Integer version);

  @StringLength(-1)
  String getUiSchema();

  void setUiSchema(String uiSchema);

  @StringLength(-1)
  String getSchema();

  void setSchema(String Schema);

  @OneToMany
  AoFedexIdea[] getIdeas();
}
