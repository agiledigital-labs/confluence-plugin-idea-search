package au.com.agiledigital.idea_search.helpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Helpers to do data transformation for macros
 */
public class MacroHelpers {
  public static Set<String> splitTrimToSet(String str, String delimiter) {
    if (StringUtils.isBlank(str)) {
      return Collections.emptySet();
    }

    return Arrays.stream(str.split(delimiter)).map((value) -> value.trim()).collect(
      Collectors.toSet());
  }
}
