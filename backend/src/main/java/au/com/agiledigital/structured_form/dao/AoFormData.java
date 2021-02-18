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
public interface AoFormData extends RawEntity<Long> {
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

  @Indexed
  String getIndexString0();
  void setIndexString0(String indexString0);

  @Indexed
  String getIndexString1();
  void setIndexString1(String indexString1);
  @Indexed
  String getIndexString2();
  void setIndexString2(String indexString2);
  @Indexed
  String getIndexString3();
  void setIndexString3(String indexString3);
  @Indexed
  String getIndexString4();
  void setIndexString4(String indexString4);


  @Indexed
  String getIndexNumber0();
  void setIndexNumber0(double indexNumber0);
  @Indexed
  String getIndexNumber1();
  void setIndexNumber1(double indexNumber1);
  @Indexed
  String getIndexNumber2();
  void setIndexNumber2(double indexNumber2);
  @Indexed
  String getIndexNumber3();
  void setIndexNumber3(double indexNumber3);
  @Indexed
  String getIndexNumber4();
  void setIndexNumber4(double indexNumber4);

}
