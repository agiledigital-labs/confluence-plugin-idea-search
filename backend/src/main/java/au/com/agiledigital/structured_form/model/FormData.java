package au.com.agiledigital.structured_form.model;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.user.ConfluenceUser;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;


import java.util.Set;


/**
 * Internal model of the idea
 */
public class FormData {

  private final long globalId;
  private final ContentId contentId;
  private final ConfluenceUser creator;
  private final String title;
  private final String formDataValue;
  private final Set<FormIndex> indexData;


  @JsonCreator
  private FormData(
    @JsonProperty("globalId") long globalId,
    @JsonProperty("contentId") ContentId contentId,
    @JsonProperty("creator") ConfluenceUser creator,
    @JsonProperty("title") String title,
    @JsonProperty("formData") String formData,
    @JsonProperty("indexData")  Set<FormIndex> indexData
  ) {
    this.globalId = globalId;
    this.contentId = contentId;
    this.creator = creator;
    this.title = title;
    this.formDataValue = formData;
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

  public String getFormDataValue() {return this.formDataValue;}

  public  Set<FormIndex> getIndexData() {return this.indexData;}

  public Object get(String key) {
    switch(key.toLowerCase()){
      case "creator":
        return this.getCreator();
      case "globalid":
        return this.getGlobalId();
      case "contentid":
        return this.getContentId();
      default :
        return this.getTitle();
    }
  }

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
    private  Set<FormIndex> indexData;

    public Builder() {
    }

    public Builder(FormData formData) {
      this.globalId = formData.globalId;
      this.contentId = formData.contentId;
      this.creator = formData.creator;
      this.title = formData.title;
      this.formData = formData.formDataValue;
      this.indexData = formData.indexData;
    }

    public FormData.Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public FormData.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    public FormData.Builder withContentId(ContentId contentId) {

      this.contentId = contentId;
      return this;
    }

    public FormData.Builder withCreator(ConfluenceUser creator) {
      this.creator = creator;
      return this;
    }

    public FormData.Builder withFormData(String formData) {
      this.formData = formData;
      return this;
    }

    public FormData.Builder withIndexData( Set<FormIndex> indexData) {
      this.indexData = indexData;
      return this;
    }

    public FormData build() {
      return new FormData(
        this.globalId,
        this.contentId,
        this.creator,
        this.title,
        this.formData,
        this.indexData);
    }
  }
}
