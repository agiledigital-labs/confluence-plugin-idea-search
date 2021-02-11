package au.com.agiledigital.idea_search.blueprints;

import java.util.Arrays;

public enum Parameter {
  IDEA_TITLE("vIdeaTitle");

  private String reference;

  public String getReference() {
    return this.reference;
  }

  Parameter(String reference) {
    this.reference = reference;
  }

}
