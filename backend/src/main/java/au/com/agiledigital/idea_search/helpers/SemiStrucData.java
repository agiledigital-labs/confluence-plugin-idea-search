package au.com.agiledigital.idea_search.helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SemiStrucData {
  @Nonnull
  private String header;
  @Nullable
  private List<String> bodyList;


  public SemiStrucData(@Nonnull String header) {
    this.header = header;
  }

  public String getHeader() {
    return this.header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public void setBody(String body) {
    assert this.bodyList != null;
    this.bodyList.add(body);
  }

  public Object getBody() {

      return this.bodyList.get(0);

  }  public Object getBodyList() {

      return this.bodyList.get(0);

  }

  public String getBodyListAsString() {


    return String.join(", ", this.bodyList);
  }

  public boolean isList() {
    return this.bodyList.size() > 1;

  }

}
