package au.com.agiledigital.idea_search.macros;

import java.util.Arrays;

public enum StructuredCategory {
  TECHNOLOGIES("technologies", "Technology");

  private String key;
  private String template;

  StructuredCategory(String key, String template) {
    this.key = key;
    this.template = template;
  }

  public String getKey() {
    return key;
  }

  public String getTemplate() {
    return "StructuredField" + template + ".vm";
  }

  public static StructuredCategory fromKey(String key) {
    return Arrays.stream(StructuredCategory.values()).filter((constant) -> constant.key.equals(key))
      .findFirst().orElse(null);
  }
}
