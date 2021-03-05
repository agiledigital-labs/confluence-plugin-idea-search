package au.com.agiledigital.structured_form.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Prepares structured data for presentation, capitalising titles
 * Takes the JSON from the macro, and puts it in the context
 * for the velocity template
 */
public class StructuredData implements Macro {
  private final Gson gson = new Gson();
  private final PageBuilderService pageBuilderService;

  public StructuredData(@ComponentImport PageBuilderService pageBuilderService){
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    pageBuilderService
      .assembler()
      .resources()
      .requireWebResource(
        "au.com.agiledigital.structured_form:test-render");

    LinkedHashMap<String, String> jsonObject = gson.fromJson(Jsoup.parse(s).body().text(), LinkedHashMap.class);
    JsonElement jsonElement = gson.toJsonTree(jsonObject);


    // making keys (section headers) capitalised. splitting on capital letters, i.e. camelCase becomes Camel Case
    Map<String, String> renderedData = getStringMap(jsonElement);

    Map<String, Object> context = new HashMap<>();
    context.put("data", renderedData);
    context.put("rawData", s);
        return VelocityUtils.getRenderedTemplate("vm/StructuredData.vm", context);
  }

  /**
   * Converts the output of gson.tojson where the json has child objects or arrays
   * into a map of key and string
   *
   * @param data map that could contain sup arrays or objects
   * @return Map of key and String
   */
  @Nonnull
  private Map<String, String> getStringMap(@Nonnull JsonElement data) {
    return data.getAsJsonObject().entrySet()
      .stream().map(entry ->
        new AbstractMap.SimpleEntry<String, String>(headingTransformation(entry.getKey()), this.handleComplexObject(entry.getValue()))
      )
      .collect(Collectors.toMap(
        AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
        (key, duplicateKey) -> {
          throw new IllegalStateException(String.format("Duplicate key [%s]", key));
        }, LinkedHashMap::new
      ));
  }

  /**
   * Converts the unknown value into string for display.
   *
   * @param displayData value from the structured data json to be converted into a string
   * @return paragraph text to be shown on the macro page
   */
  private String handleComplexObject(@Nonnull JsonElement displayData) {
    if (displayData.isJsonNull()) {
      return "No data on this element";
    } else if (displayData.isJsonPrimitive()) {
      return displayData.getAsString();
    } else if (displayData.isJsonArray()) {
      return "[" + StringUtils.join(
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
          displayData.getAsJsonArray().iterator(), Spliterator.ORDERED), false)
          .map(this::handleComplexObject).collect(Collectors.toList()), ", ") +"]";
    } else if (displayData.isJsonObject()) {
      return StringUtils.join(
        displayData.getAsJsonObject().entrySet()
          .stream()
          .map(element -> new AbstractMap.SimpleEntry<>(element.getKey(), handleComplexObject(element.getValue())))
          .map(stringSimpleEntry -> stringSimpleEntry.getKey() + " -> " + stringSimpleEntry.getValue())
          .collect(Collectors.toList()), ", ");
    } else {
      return "failed to load";
    }
  }

  /**
   * @param heading string in camelCase to be converted to title case
   * @return title case string
   */
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

  @Nonnull
  @Override
  public BodyType getBodyType() {
    return BodyType.RICH_TEXT;
  }

  @Nonnull
  @Override
  public OutputType getOutputType() {
    return OutputType.BLOCK;
  }
}
