package au.com.agiledigital.idea_search.model;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Internal model of the technology
 */
@Immutable
public class FedexTechnology {

  private final long globalId;
  private final String technology;

  @JsonCreator
  public FedexTechnology(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("technology") String technology
  ) {
    this.globalId = globalId;
    this.technology = technology;
  }

  public long getGlobalId() {
    return this.globalId;
  }

  public String getTechnology() {
    return this.technology;
  }

  public static class Builder {

    private long globalId;
    private String technology;

    public Builder() {
    }

    public Builder(FedexTechnology fedexTechnology) {
      this.technology = fedexTechnology.technology;
      this.globalId = fedexTechnology.globalId;
    }

    public FedexTechnology.Builder withTechnology(String technology) {
      this.technology = technology;
      return this;
    }

    public FedexTechnology.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FedexTechnology build() {
      return new FedexTechnology(this.globalId, this.technology);
    }
  }
}
