package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.macros.MacroHelpers.splitTrimToSet;

import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.HashMap;
import java.util.Map;

public class StructureFieldRenderHelper {
  public static String render(StructuredCategory category, String body) {
    return render(category, body, true);
  }

  public static String render(StructuredCategory category, String body, boolean heading) {
    Map<String, Object> context = new HashMap<>();

    context.put("heading", heading);

    switch (category) {
      case TECHNOLOGIES:
        context.put("payload", splitTrimToSet(body, ","));
        break;
    }

    return VelocityUtils.getRenderedTemplate(
      "vm/" + category.getTemplate(),
      context
    );
  }
}
