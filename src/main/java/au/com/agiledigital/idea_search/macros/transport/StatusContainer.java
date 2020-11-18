package au.com.agiledigital.idea_search.macros.transport;

import au.com.agiledigital.idea_search.Status;

/**
 * Container of status metadata to be used within a velocity template
 */
public class StatusContainer {

  private Status status;

  public StatusContainer(Status status) {
    this.status = status;
  }

  public String getKey() {
    return status.getReferenceName();
  }

  public String getHuman() {
    return status.getHumanName();
  }
}
