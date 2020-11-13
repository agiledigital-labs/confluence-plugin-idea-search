package au.com.agiledigital.idea_search;

import java.util.Arrays;

public enum Parameter {
  IDEA_TITLE("vIdeaTitle"),
  IDEA_DESCRIPTION("vIdeaDescription"),
  IDEA_OWNER("vIdeaOwner"),
  IDEA_TEAM("vIdeaTeam"),
  IDEA_STATUS("vIdeaStatus");

  public String reference;

  Parameter(String reference) {
    this.reference = reference;
  }

  public static Parameter getParameterFromReference(String referenceName) {
    return Arrays
      .stream(Parameter.values())
      .filter(status -> status.reference.equals(referenceName))
      .findFirst()
      .orElse(null);
  }
}
