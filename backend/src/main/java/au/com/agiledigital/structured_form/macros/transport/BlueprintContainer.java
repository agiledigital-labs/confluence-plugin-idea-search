package au.com.agiledigital.structured_form.macros.transport;

/**
 * Container of blueprint metadata to be used within a velocity template
 */
public class BlueprintContainer {

  private final String spaceKey;
  private final String baseUrl;
  private final String blueprintId;

  public BlueprintContainer(String spaceKey, String baseUrl, String blueprintId) {
    this.spaceKey = spaceKey;
    this.baseUrl = baseUrl;
    this.blueprintId = blueprintId;
  }

  public String getSpaceKey() {
    return spaceKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getBlueprintId() {
    return blueprintId;
  }
}
