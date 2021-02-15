package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.google.gson.Gson;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

/**
 * Prepares structured data for presentation, capitalising titles
 */
public class StructuredData implements Macro {
  private Gson gson = new Gson();

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext) {
    // gets the page body data as mapped
    LinkedHashMap<String, String> data =gson.fromJson(Jsoup.parse(s).body().text(), LinkedHashMap.class);

    // making keys (section headers) capitalised. splitting on capital letters, i.e. camelCase becomes Camel Case
    Map<String, String> renderedData = data.entrySet()
      .stream().map(
        entry -> new AbstractMap.SimpleEntry<>(headingTransformation(entry.getKey()), entry.getValue())
      )
      .collect(
        Collectors.toMap(
          entry -> entry.getKey(), entry->entry.getValue(),
          (key, duplicateKey) -> {
            throw new IllegalStateException(String.format("Duplicate key [%s]", key));
          }, LinkedHashMap::new
        )
      );

    Map<String, Object> context = new HashMap<>();
    context.put("data", renderedData);
    return VelocityUtils.getRenderedTemplate("vm/StructuredData.vm", context);
  }

  // converts a string into a capitalised string and splits on capital letters
  private String headingTransformation(String heading) {
    String[] headingList = StringUtils.splitByCharacterTypeCamelCase(heading);

    // return an empty string if the array is empty
    if (!ArrayUtils.isNotEmpty(headingList)) {
      return "";
    }

    // to make sure the first letter of the heading is always capitalised
    headingList[0] = StringUtils.capitalize(headingList[0]);
    return StringUtils.join(headingList, " ");
  }

  @Override
  public BodyType getBodyType() {
    return BodyType.RICH_TEXT;
  }

  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
