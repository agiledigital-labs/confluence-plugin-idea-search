package au.com.agiledigital.idea_search.helpers;

import au.com.agiledigital.idea_search.Status;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import au.com.agiledigital.idea_search.macros.transport.StatusContainer;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
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
public class StructureFieldRenderHelper {

  private StructureFieldRenderHelper() {
    throw new IllegalStateException("Structure Field Render Helper class");
  }

  private static final Logger log = LoggerFactory.getLogger(StructureFieldRenderHelper.class);

  private static final String PAYLOAD = "payload";

  public static String render(StructuredCategory category, String body) {

    return render(category, body, true, null);
  }

  public static String render(
    StructuredCategory category, String body, boolean heading, XhtmlContent xhtmlContent) {
    Map<String, Object> context = new HashMap<>();

    context.put("heading", heading);

    switch (category) {
      case TECHNOLOGIES:
        /** Splits the comma separated string into a list and replaces all html tags */
        context.put(PAYLOAD, splitTrimToSet(body, ",").stream().map(Utilities::removeTags));
        break;
      case DESCRIPTION:
      case OWNER:
      case TEAM:
        String bodyConverted = body;

        if (xhtmlContent != null) {
          try {
            bodyConverted =
              xhtmlContent.convertStorageToView(
                body, new DefaultConversionContext(new RenderContext()));
          } catch (XMLStreamException | XhtmlException e) {
            log.warn(e.toString());
          }
        }

        context.put(PAYLOAD, bodyConverted);
        break;
      case STATUS:
        context.put(PAYLOAD, new StatusContainer(Status.getStatusFromReference(body)));
        break;
      default:
        break;
    }

    return VelocityUtils.getRenderedTemplate("vm/" + category.getTemplate(), context);
  }
}
