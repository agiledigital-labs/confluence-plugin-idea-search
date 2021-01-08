package au.com.agiledigital.idea_search.helpers;

public class utilities {

  /**
   * Remove tags (<tagName></tagName>) and give only wrapped text
   *
   * @param rawData the string to be stripped off tags
   * @return string without any tags wrapping it
   */
  public static String removeTags(String rawData) {
    return rawData.replaceAll("\\<.*?\\>", "");
  }
}
