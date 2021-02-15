package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Prepares structured data for presentation,
 *
 * Takes the JSON from the macro, and puts it in the context
 * for the velocity template
 *
 */
public class StructuredData implements Macro {
  private Gson gson = new Gson();
  private final PageBuilderService pageBuilderService;

  public StructuredData(
    @ComponentImport PageBuilderService pageBuilderService) {
    this.pageBuilderService = pageBuilderService;
  }

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    Map<String, String> data = new LinkedHashMap<>();
    // gets the page body data as mapped
    data =gson.fromJson(Jsoup.parse(s).body().text(), data.getClass());

    // making keys (section headers) capitalised. splitting on capital letters, i.e. camelCase becomes Camel Case
    Map<String, String> renderedData = getStringMap(data);

    Map<String, Object> context = new HashMap<>();
    context.put("data", renderedData);
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
  private Map<String, String> getStringMap(Map<String, ?> data) {
    return data.entrySet()
      .stream().map(entry ->
        new String[]{headingTransformation(entry.getKey()), this.handleComplexObject(entry.getValue())}
      )
      .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
  }

  /**
   * Converts values in a set to a List of strings where key and value are joined, separated by :
   *
   * @param data set of key values pairs
   * @return List<String> where key and value are concatenated
   */
  @Nonnull
  private List<String> getSubHeadings(Set data) {
   return  (List<String>) data.stream().map(r ->  ((Map.Entry) r).getKey() +": " + this.handleComplexObject(((Map.Entry<?, ?>) r).getValue()) + ", ").collect(Collectors.toList());
  }

  /**
   * Converts the unknown value into string for display.
   *
   * @param displayData value from the structured data json to be converted into a string
   * @return paragraph text to be shown on the macro page
   */
  private String handleComplexObject(Object displayData){
    if (String.class.equals(displayData.getClass())) {
      return (String) displayData;
    } else if (ArrayList.class.equals(displayData.getClass())) {
      return  StringUtils.join(((ArrayList<?>) displayData).toArray(), ", ");
    } else if (LinkedTreeMap.class.equals(displayData.getClass())) {
      return StringUtils.join(getSubHeadings(((LinkedTreeMap<String, String >) displayData).entrySet()).toArray());
    } else {
      return "failed to load";
    }

  }

  /**
   *
   * @param heading string in camelCase to be converted to title case
   * @return
   */
  private String headingTransformation(String heading) {
    String[] headingList = StringUtils.splitByCharacterTypeCamelCase(heading);
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