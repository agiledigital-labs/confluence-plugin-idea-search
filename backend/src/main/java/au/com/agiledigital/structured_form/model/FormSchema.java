package au.com.agiledigital.structured_form.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Internal model of the technology
 */
public class FormSchema {

  private final long globalId;
  private final String name;
  private String uiSchema;
  private String schema;
  private String indexSchema;
  private final String description;
  private final Integer version;
  private final List<FormData> formDataList;
  private final boolean isDufault;

  @JsonCreator
  public FormSchema(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("name") String name,
    @JsonProperty("uiSchema") String uiSchema,
    @JsonProperty("schema") String schema,
    @JsonProperty("indexSchema") String indexSchema,
    @JsonProperty("version") Integer version,
    @JsonProperty("description") String description,
    @JsonProperty("formDataList") List<FormData> formDataList,
    @JsonProperty("isDefault") boolean isDufault
  ) {
    this.globalId = globalId;
    this.name = name;
    this.uiSchema = uiSchema;
    this.schema = schema;
    this.indexSchema = indexSchema;
    this.version = version;
    this.description = description;
    this.formDataList = formDataList;
    this.isDufault = isDufault;
  }

  public long getGlobalId() {
    return this.globalId;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public String getIndexSchema() {
    return this.indexSchema;
  }


  public String getSchema() {
    return this.schema;
  }


  public Integer getVersion() {
    return this.version;
  }

  public String getUiSchema() {
    return this.uiSchema;
  }

  public boolean getIsDefault() {return this.isDufault;}

  public List<FormData> getChildForms() { return  this.formDataList;}

  public static class Builder {

    private long globalId;
    private String name;
    private Integer version;
    private String uiSchema;
    private String schema;
    private String indexSchema;
    private String description;
    private List<FormData> formDataList;
    private boolean isDefault;

    public Builder() {
    }

    public Builder(@Nonnull FormSchema formSchema) {
      this.name = formSchema.name;
      this.globalId = formSchema.globalId;
      this.schema = formSchema.schema;
      this.uiSchema = formSchema.uiSchema;
      this.indexSchema = formSchema.indexSchema;
      this.version = formSchema.version;
      this.description = formSchema.description;
      this.formDataList = formSchema.formDataList;
      this.isDefault = formSchema.isDufault;
    }

    @Nonnull
    public FormSchema.Builder withName(String name) {
      this.name = name;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withUiSchema(String uiSchema) {
      this.uiSchema = uiSchema;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withSchema(String schema) {
      this.schema = schema;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withIndexSchema(String indexSchema) {
      this.indexSchema = indexSchema;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    @Nonnull
    public FormSchema.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FormSchema.Builder withFormDataList(List<FormData> formDataList) {
      this.formDataList = formDataList;
      return this;
    }

    public FormSchema.Builder withIsDefault(boolean isDefault){
      this.isDefault = isDefault;
      return this;
    }

    @Nonnull
    public FormSchema build() {
      return new FormSchema(
        this.globalId,
        this.name,
        this.uiSchema,
        this.schema,
        this.indexSchema,
        this.version,
        this.description,
        this.formDataList,
        this.isDefault);
    }
  }
}
