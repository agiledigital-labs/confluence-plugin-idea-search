package au.com.agiledigital.idea_search.helpers;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helpers to do data transformation for macros
 */
public class MacroHelpers {

  private MacroHelpers() {
    throw new IllegalStateException("Macro Helper class");
  }

  /**
   * Splits a string at a delimiter, then remove the starting and ending whitespace from each piece
   *
   * @param str       value to be split
   * @param delimiter search key to split the string on
   * @return Set of the sections
   */
  public static Set<String> splitTrimToSet(String str, String delimiter) {
    if (StringUtils.isBlank(str)) {
      return Collections.emptySet();
    }

    return Arrays.stream(str.split(delimiter))
      .map(String::trim)
      .collect(Collectors.toSet());
  }
}
