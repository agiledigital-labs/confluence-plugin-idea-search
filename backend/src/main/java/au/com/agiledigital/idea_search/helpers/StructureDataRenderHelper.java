package au.com.agiledigital.idea_search.helpers;

import au.com.agiledigital.idea_search.Status;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import au.com.agiledigital.idea_search.macros.transport.StatusContainer;
import au.com.agiledigital.idea_search.service.FedexIdeaService;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.service.DefaultPageService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.pages.DefaultPageManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import java.util.Map;

import static au.com.agiledigital.idea_search.helpers.MacroHelpers.splitTrimToSet;

/**
 * Velocity template renderer helper for Structured Fields. Transforms the body into a usable format
 * for the template based off the category
 */
public class StructureDataRenderHelper {

  private StructureDataRenderHelper() {
    throw new IllegalStateException("Structure Field Render Helper class");
  }

  private static final Logger log = LoggerFactory.getLogger(StructureDataRenderHelper.class);

  private static final String PAYLOAD = "data";

  public static String render(StructuredCategory category, String body) {

    return render(category, body, true, null);
  }

  public static String render(
    StructuredCategory category, String body, boolean heading, XhtmlContent xhtmlContent) {
    Map<String, Object> context = new HashMap<>();

    context.put("heading", heading);

    switch (category) {

      case IDEA_V1:

//        context.put();

        break;
      default:
        context.put(PAYLOAD, splitTrimToSet(body, ",").stream().map(Utilities::removeTags));
        break;
    }

    return VelocityUtils.getRenderedTemplate("vm/" + category.getTemplate(), context);
  }
}
