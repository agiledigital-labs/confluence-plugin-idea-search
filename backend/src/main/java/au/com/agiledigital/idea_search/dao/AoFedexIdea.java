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
public interface AoFedexIdea extends RawEntity<Long> {
  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();

  String getOwner();

  void setOwner(String owner);

  String getVersion();

  void setVersion(String version);

  @StringLength(-1)
  String getTitle();

  void setTitle(String ideaTitle);

  @Indexed
  long getContentId();

  void setContentId(long contentId);

  @OneToMany
  AoFedexTechnology[] getTechnologies();

  @StringLength(-1)
  String getStatus();

  void setStatus(String status);

  @StringLength(-1)
  String getDescription();

  void setDescription(String team);

  @Indexed
  String getCreatorUserKey();

  void setCreatorUserKey(String creatorUserKey);


  @StringLength(-1)
  String getFormData();

  void setFormData(String formData);
}
