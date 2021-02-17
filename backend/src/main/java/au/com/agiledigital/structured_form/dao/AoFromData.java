package au.com.agiledigital.structured_form.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.*;

/**
 * FormData data store object,
 *
 * DAO interface used by active objects to create the data store tables
 *
 * OneToMany annotation creates PK/FK relation between the two tables
 *
 */
public interface AoFromData extends RawEntity<Long> {
  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();

  @StringLength(-1)
  String getTitle();

  void setTitle(String ideaTitle);

  @Indexed
  long getContentId();

  void setContentId(long contentId);

  @Indexed
  String getCreatorUserKey();

  void setCreatorUserKey(String creatorUserKey);


  @StringLength(-1)
  String getFormData();

  void setFormData(String formData);
}
