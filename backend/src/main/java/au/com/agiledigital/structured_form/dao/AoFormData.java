package au.com.agiledigital.structured_form.dao;

import net.java.ao.RawEntity;
import net.java.ao.schema.*;

import javax.annotation.Nonnull;

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

  @Nonnull
  @StringLength(-1)
  String getTitle();
  void setTitle(String title);

  @Indexed
  long getContentId();
  void setContentId(long contentId);

  @Nonnull
  @Indexed
  String getCreatorUserKey();
  void setCreatorUserKey(String creatorUserKey);


  @Nonnull
  @StringLength(-1)
  String getFormData();
  void setFormData(String formData);

  @Nonnull
  @Indexed
  String getIndexString0();
  void setIndexString0(String indexString0);
  @Nonnull
  @Indexed
  String getIndexString1();
  void setIndexString1(String indexString1);
  @Nonnull
  @Indexed
  String getIndexString2();
  void setIndexString2(String indexString2);
  @Nonnull
  @Indexed
  String getIndexString3();
  void setIndexString3(String indexString3);
  @Nonnull
  @Indexed
  String getIndexString4();
  void setIndexString4(String indexString4);


  @Nonnull
  @Indexed
  String getIndexNumber0();
  void setIndexNumber0(double indexNumber0);
  @Nonnull
  @Indexed
  String getIndexNumber1();
  void setIndexNumber1(double indexNumber1);
  @Nonnull
  @Indexed
  String getIndexNumber2();
  void setIndexNumber2(double indexNumber2);
  @Nonnull
  @Indexed
  String getIndexNumber3();
  void setIndexNumber3(double indexNumber3);
  @Nonnull
  @Indexed
  String getIndexNumber4();
  void setIndexNumber4(double indexNumber4);

  @Nonnull
  @Indexed
  String getIndexBoolean0();
  void setIndexBoolean0(boolean indexBoolean0);
  @Nonnull
  @Indexed
  String getIndexBoolean1();
  void setIndexBoolean1(boolean indexBoolean1);
  @Nonnull
  @Indexed
  String getIndexBoolean2();
  void setIndexBoolean2(boolean indexBoolean2);
  @Nonnull
  @Indexed
  String getIndexBoolean3();
  void setIndexBoolean3(boolean indexBoolean3);
  @Nonnull
  @Indexed
  String getIndexBoolean4();
  void setIndexBoolean4(boolean indexBoolean4);

}
