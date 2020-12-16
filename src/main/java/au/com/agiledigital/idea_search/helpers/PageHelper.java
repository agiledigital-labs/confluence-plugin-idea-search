package au.com.agiledigital.idea_search.helpers;

public class PageHelper {

  public PageHelper() {
  }

  /**
   * Wraps Confluence storage format with a root element and doctype defining custom DTD definition
   *
   * @param body confluence storage format body
   * @return Wrapped body
   */
  public static String wrapBody(String body) {
    return ("<!DOCTYPE html [ <!ENTITY nbsp \"&#160;\"> ]><ac:confluence>"
      + body
      + "</ac:confluence>");
  }
}
