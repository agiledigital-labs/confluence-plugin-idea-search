package au.com.agiledigital.idea_search.macros;

import java.util.Arrays;


/**
 * Categories of structure data for the Structured Field macro
 */
public enum StructuredCategory {
  TECHNOLOGIES("technologies", "Technology", "Add your technologies"),
  DESCRIPTION("description", "Description", "It is awesome, how could it not be"),
  STATUS("status", "Status", "new"),
  OWNER("owner", "Owner", "No one :("),
  BLUEPRINT_ID("blueprintId", "", "");

  private String key;
  private String template;
  private String fallbackText;

  StructuredCategory(String key, String template, String fallbackText) {
    this.key = key;
    this.template = template;
    this.fallbackText = fallbackText;
  }

  public static StructuredCategory fromKey(String key) {
    return Arrays.stream(StructuredCategory.values()).filter((constant) -> constant.key.equals(key))
      .findFirst().orElse(null);
  }

  public String getKey() {
    return key;
  }

  public String getTemplate() {
    return "StructuredField" + template + ".vm";
  }

  public String getFallbackText() {
    return fallbackText;
  }
}
