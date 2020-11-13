package au.com.agiledigital.idea_search;

import java.util.Arrays;

public enum Status {
  NEW("New", "new"),
  IN_PROGRESS("In progress", "inProgress"),
  COMPLETED("Completed", "completed");

  private String humanName;
  private String referenceName;

  Status(String humanName, String referenceName) {
    this.humanName = humanName;
    this.referenceName = referenceName;
  }

  public static Status getStatusFromReference(String referenceName) {
    return Arrays
      .stream(Status.values())
      .filter(status -> status.referenceName.equals(referenceName))
      .findFirst()
      .orElse(null);
  }

  public String getHumanName() {
    return humanName;
  }

  public String getReferenceName() {
    return referenceName;
  }
}
