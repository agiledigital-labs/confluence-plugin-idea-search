package au.com.agiledigital.structured_form.helpers;

import javax.annotation.Nonnull;

public class PageHelper {

  private PageHelper() {throw new IllegalStateException("Page Helper class"); }

  /**
   * Wraps Confluence storage format with a root element and doctype defining custom DTD definition
   *
   * @param body confluence storage format body
   * @return Wrapped body
   */
  @Nonnull
  public static String wrapBody(String body) {
    return ("<!DOCTYPE html [ <!ENTITY nbsp \"&#160;\"> ]><ac:confluence>"
      + body
      + "</ac:confluence>");
  }
}
