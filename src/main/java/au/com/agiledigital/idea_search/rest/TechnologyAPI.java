package au.com.agiledigital.idea_search.rest;

/**
 * Model for external API technology response
 *
 */
public class TechnologyAPI {

  public String label;

  /**
   * Create object that can be consumed by the AUI select input
   *
   * @param label
   */
  public TechnologyAPI(String label) {
    this.label = label;
  }
}
