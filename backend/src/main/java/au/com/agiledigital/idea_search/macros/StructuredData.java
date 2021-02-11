package au.com.agiledigital.idea_search.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;

public class StructuredData implements Macro {
  private Gson gson = new Gson();

  @Override
  public String execute(Map<String, String> map, String s, ConversionContext conversionContext)
    throws MacroExecutionException {
    Map<String, String> data = new LinkedHashMap<>();
    // gets the page body data as mapped
    data =gson.fromJson(Jsoup.parse(s).body().text(), data.getClass());

    Map<String, String> renderData = new LinkedHashMap<>();
    // capitalises text values
    data.forEach( (r, t) -> {
      renderData.put(WordUtils.capitalize(r.replaceAll("[A-Z]", " $0")), t);
    });

    Map<String, Object> context = new HashMap<>();
    context.put("data", renderData);
    return VelocityUtils.getRenderedTemplate("vm/StructuredData.vm", context);
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