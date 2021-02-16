package au.com.agiledigital.idea_search.model;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.user.ConfluenceUser;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;


import java.util.List;


/**
 * Internal model of the idea
 */
public class FedexIdea {

  private final long globalId;
  private final ContentId contentId;
  private final ConfluenceUser creator;
  private final String title;
  private final String formData;
  private List<String> indexData;


  @JsonCreator
  private FedexIdea(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("contentId") ContentId contentId,
    @JsonProperty("creator") ConfluenceUser creator,
    @JsonProperty("title") String title,
    @JsonProperty("formData") String formData,
    @JsonProperty("indexData") List<String> indexData
  ) {
    this.globalId = globalId;
    this.contentId = contentId;
    this.creator = creator;
    this.title = title;
    this.formData = formData;
    this.indexData = indexData;
  }

  public long getGlobalId() {
    return this.globalId;
  }


  public ContentId getContentId() {
    return this.contentId;
  }

  public ConfluenceUser getCreator() {
    return this.creator;
  }

  public String getTitle() {
    return this.title;
  }

  public String getFormData() {return this.formData;}

  public List<String> getIndexData() {return this.indexData;}

  public String toString() {
    return ("Idea [globalId="
      + this.globalId
      + ", contentId="
      + this.contentId
      + ", creator="
      + this.creator
      + ", title="
      + this.title
      + ", indexData="
      + this.indexData
      + "]");
  }

  public static class Builder {

    private long globalId;
    private ContentId contentId;
    private ConfluenceUser creator;
    private String title;
    private String formData;
    private List<String> indexData;

    public Builder() {
    }

    public Builder(FedexIdea fedexIdea) {
      this.globalId = fedexIdea.globalId;
      this.contentId = fedexIdea.contentId;
      this.creator = fedexIdea.creator;
      this.title = fedexIdea.title;
      this.formData = fedexIdea.formData;
      this.indexData = fedexIdea.indexData;
    }

    public FedexIdea.Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public FedexIdea.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FedexIdea.Builder withContentId(ContentId contentId) {

      this.contentId = contentId;
      return this;
    }

    public FedexIdea.Builder withCreator(ConfluenceUser creator) {
      this.creator = creator;
      return this;
    }

    public FedexIdea.Builder withFormData(String formData) {
      this.formData = formData;
      return this;
    }

    public FedexIdea.Builder withIndexData(List<String> indexData) {
      this.indexData = indexData;
      return this;
    }

    public FedexIdea build() {
      return new FedexIdea(
        this.globalId,
        this.contentId,
        this.creator,
        this.title,
        this.formData,
        this.indexData);
    }
  }
}
