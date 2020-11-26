package au.com.agiledigital.idea_search.model;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class FedexTechnology {

  private final long globalId;
  private final String technology;

  public FedexTechnology(long globalId, String technology) {
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

    public Builder() {}

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
