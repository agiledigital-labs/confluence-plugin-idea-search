package au.com.agiledigital.idea_search.model;

import java.beans.PropertyChangeListener;
import java.util.List;
import jdk.nashorn.internal.ir.annotations.Immutable;
import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.beans.PropertyChangeListener;
import java.util.List;

@Immutable
public class FedexIdea {

  private final long globalId;
  private final String owner;
  private final long contentId;
  private final List<FedexTechnology> technologies;
  private final String creator;
  private final String title;
  private final String description;
  private final String status;

  @JsonCreator
  private FedexIdea(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("owner") String owner,
    @JsonProperty("contentId") long contentId,
    @JsonProperty("technologies") List<FedexTechnology> technologies,
    @JsonProperty("creator") String creator,
    @JsonProperty("title") String title,
    @JsonProperty("status") String status,
    @JsonProperty("description") String description
  ) {
    this.globalId = globalId;
    this.owner = owner;
    this.contentId = contentId;
    this.technologies = technologies;
    this.creator = creator;
    this.title = title;
    this.description = description;
    this.status = status;
  }

  public long getGlobalId() {
    return this.globalId;
  }

  public String getOwner() {
    return this.owner;
  }

  public long getContentId() {
    return this.contentId;
  }

  public List<FedexTechnology> getTechnologies() {
    return this.technologies;
  }

  public String getCreator() {
    return this.creator;
  }

  public String getTitle() {
    return this.title;
  }

  public String getStatus() {
    return this.status;
  }

  public String getDescription() {
    return this.description;
  }

  public String toString() {
    return (
      "Idea [globalId=" +
      this.globalId +
      ", owner=" +
      this.owner +
      ", contentId=" +
      this.contentId +
      ", technologies=" +
      this.technologies +
      ", creator=" +
      this.creator +
      ", title=" +
      this.title +
      ", description=" +
      this.description +
      ", status=" +
      this.status +
      "]"
    );
  }

  public static class Builder {

    private long globalId;
    private String owner;
    private long contentId;
    private List<FedexTechnology> technologies;
    private String creator;
    private String title;
    private String status;
    private String description;

    public Builder() {}

    public Builder(FedexIdea fedexIdea) {
      this.globalId = fedexIdea.globalId;
      this.owner = fedexIdea.owner;
      this.contentId = fedexIdea.contentId;
      this.technologies = fedexIdea.technologies;
      this.creator = fedexIdea.creator;
      this.title = fedexIdea.title;
      this.status = fedexIdea.status;
      this.description = fedexIdea.description;
    }

    public FedexIdea.Builder withTechnologies(
      List<FedexTechnology> technologies
    ) {
      this.technologies = technologies;
      return this;
    }

    public FedexIdea.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FedexIdea.Builder withOwner(String owner) {
      this.owner = owner;
      return this;
    }

    public FedexIdea.Builder withContentId(long contentId) {
      this.contentId = contentId;
      return this;
    }

    public FedexIdea.Builder withCreator(String creator) {
      this.creator = creator;
      return this;
    }

    public FedexIdea.Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public FedexIdea.Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    public FedexIdea build() {
      return new FedexIdea(
        this.globalId,
        this.owner,
        this.contentId,
        this.technologies,
        this.creator,
        this.title,
        this.status,
        this.description
      );
    }
  }
}
