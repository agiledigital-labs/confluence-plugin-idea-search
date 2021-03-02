package au.com.agiledigital.structured_form.model;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;


import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;


/**
 * Internal model of the form data
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

  /**
   * Return global id of form data
   *
   * @return globalId
   */
  public long getGlobalId() {
    return this.globalId;
  }

  /**
   * Return content id of form data
   *
   * @return contentId
   */
  public ContentId getContentId() {
    return this.contentId;
  }

  /**
   * Get the creator of the form page
   *
   * @return ConfluenceUser
   */
  public String getCreator() {
    if(this.creator != null){
      return this.creator.getName();
    } else {
      return "";
    }
  }
  /**
   * Get the creator key of the form page
   *
   * @return ConfluenceUser
   */
  public String getCreatorKey() {
    if(this.creator != null){
      return this.creator.getKey().toString();
    } else {
      return "";
    }
  }

  /**
   * Get the title of the page containing the form
   *
   * @return string of the page title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Get the form data
   *
   * @return form data as a string
   */
  public String getFormDataValue() {return this.formDataValue;}

  /**
   * Get the index data for this form
   *
   * @return set of the formIndex
   */
  public  Set<FormIndex> getIndexData() {
    if (this.indexData != null){
      return this.indexData;
    }else{
    return Collections.emptySet();
    }
  }

  /**
   * Get static items of information from the form data
   *
   * @return object relating to the key
   */
  public Object get(@Nonnull String key) {
    switch(key.toLowerCase()){
      case "creator":
        return this.getCreator();
      case "globalid":
        return this.getGlobalId();
      case "contentid":
        return this.getContentId();
      case  "title":
        return this.getTitle();
      default:
        return "";
    }
  }

  /**
   * Get the information in this object as a string
   *
   * @return String that contains all of the form data
   */
  @Nonnull
  public String toString() {
    return ("FormData [globalId="
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

  /**
   * Return global id of form data
   *
   * @return globalId
   */
  public static class Builder {

    private long globalId;
    private ContentId contentId;
    private ConfluenceUser creator;
    private String title;
    private String formData;
    private  Set<FormIndex> indexData;

    public Builder() {
    }

    public Builder(@Nonnull FormData formData) {
      this.globalId = formData.globalId;
      this.contentId = formData.contentId;
      this.creator = formData.creator;
      this.title = formData.title;
      this.formData = formData.formDataValue;
      this.indexData = formData.indexData;
    }

    @Nonnull
    public FormData.Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    @Nonnull
    public FormData.Builder withGlobalId(long globalId) {
      this.globalId = globalId;
      return this;
    }

    @Nonnull
    public FormData.Builder withContentId(ContentId contentId) {

      this.contentId = contentId;
      return this;
    }

    @Nonnull
    public FormData.Builder withCreator(ConfluenceUser creator) {
      this.creator = creator;
      return this;
    }

    @Nonnull
    public FormData.Builder withFormData(String formData) {
      this.formData = formData;
      return this;
    }

    @Nonnull
    public FormData.Builder withIndexData(Set<FormIndex> indexData) {
      this.indexData = indexData;
      return this;
    }

    @Nonnull
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
