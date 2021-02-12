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
 * Prepares structured data for presentation, capitalising titles
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
  @Nonnull
  private Map<String, String> getStringMap(Map<String, String> data) {
    return data.entrySet()
      .stream().map(entry ->
        new String[]{headingTransformation(entry.getKey()), this.handelComplexObject(entry.getValue())}
      )
      .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
  }
  @Nonnull
  private List<String> getSubHeadings(Set data) {
   return  (List<String>) data.stream().map(r ->  ((Map.Entry) r).getKey() +": " + this.handelComplexObject(((Map.Entry<?, ?>) r).getValue()) + ", ").collect(Collectors.toList());
  }

  private String handelComplexObject(Object var){
    if (String.class.equals(var.getClass())) {
      return (String) var;
    } else if (ArrayList.class.equals(var.getClass())) {
      return  StringUtils.join(((ArrayList<?>) var).toArray(), ", ");
    } else if (LinkedTreeMap.class.equals(var.getClass())) {
      return StringUtils.join(getSubHeadings(((LinkedTreeMap<String, String >) var).entrySet()).toArray());
    } else {
      return "failed to load";
    }

  }

  // converts a string into a capitalised string and splits on capital letters
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