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

  /**
   * Get the key of the status
   * @return status key value
   */
  public String getKey() {
    return status.getReferenceName();
  }

  /**
   * Get human readable Status string
   * @return human readable string
   */
  public String getHuman() {
    return status.getHumanName();
  }
}
