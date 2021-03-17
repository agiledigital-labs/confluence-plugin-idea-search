package au.com.agiledigital.structured_form.dao;

import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

import javax.annotation.Nonnull;

/**
 * FormData data store object,
 *
 * DAO interface used by active objects to create the data store tables
 *
 * OneToMany annotation creates PK/FK relation between the two tables
 *
 */
public interface AoFormSchema extends RawEntity<Long> {
  @AutoIncrement
  @NotNull
  @PrimaryKey
  long getGlobalId();


  @Nonnull
  @StringLength(-1)
  String getDescription();
  void setDescription(String team);

  @Nonnull
  @StringLength(100)
  String getName();
  void setName(String name);

  @Nonnull
  Integer getVersion();
  void setVersion(Integer version);

  @Nonnull
  @StringLength(-1)
  String getUiSchema();

  void setUiSchema(String uiSchema);

  @Nonnull
  @StringLength(-1)
  String getIndexSchema();

  void setIndexSchema(String indexSchema);

  @Nonnull
  @StringLength(-1)
  String getSchema();

  void setSchema(String schema);

  @OneToMany(reverse = "getFormSchema")
  AoFormData[] getFormData();

  @StringLength(1)
  String getIsDefault();
  void setIsDefault(String isDefault);

}
