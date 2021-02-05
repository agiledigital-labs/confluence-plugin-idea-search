package au.com.agiledigital.idea_search.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Internal model of the technology
 */
public class FedexSchema {

  private final long globalId;
  private final String name;
  private String uiSchema;
  private String schema;
  private String indexSchema;
  private final String description;
  private final Integer version;

  @JsonCreator
  public FedexSchema(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("name") String name,
    @JsonProperty("uiSchema") String uiSchema,
    @JsonProperty("schema") String schema,
    @JsonProperty("indexSchema") String indexSchema,
    @JsonProperty("version") Integer version,
    @JsonProperty("description") String description
  ) {
    this.globalId = globalId;
    this.name = name;
    this.uiSchema = uiSchema;
    this.schema = schema;
    this.indexSchema = indexSchema;
    this.version = version;
    this.description = description;
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

  public void setIndexSchema(String indexSchema) {
    this.indexSchema = indexSchema;
  }

  public String getSchema() {
    return this.schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public Integer getVersion() {
    return this.version;
  }
  public String getUiSchema() {
    return this.uiSchema;
  }

  public void setUiSchema(String uiSchema){
    this.uiSchema = uiSchema;
  }

  public static class Builder {

    private long globalId;
    private String name;
    private Integer version;
    private String uiSchema;
    private String schema;
    private String indexSchema;
    private String description;

    public Builder() {
    }

    public Builder(FedexSchema fedexSchema) {
      this.name = fedexSchema.name;
      this.globalId = fedexSchema.globalId;
      this.schema = fedexSchema.schema;
      this.uiSchema = fedexSchema.uiSchema;
      this.indexSchema = fedexSchema.indexSchema;
      this.version = fedexSchema.version;
      this.description = fedexSchema.description;
    }

    public FedexSchema.Builder withName(String name) {
      this.name = name;
      return this;
    }

    public FedexSchema.Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public FedexSchema.Builder withUiSchema(String uiSchema) {
      this.uiSchema = uiSchema;
      return this;
    }

    public FedexSchema.Builder withSchema(String schema) {
      this.schema = schema;
      return this;
    }

    public FedexSchema.Builder withIndexSchema(String indexSchema) {
      this.indexSchema = indexSchema;
      return this;
    }

    public FedexSchema.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public FedexSchema.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FedexSchema build() {
      return new FedexSchema(this.globalId, this.name, this.uiSchema, this.schema, this.indexSchema, this.version, this.description);
    }
  }
}
