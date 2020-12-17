package au.com.agiledigital.idea_search.helpers;

import static au.com.agiledigital.idea_search.helpers.MacroHelpers.splitTrimToSet;
import static au.com.agiledigital.idea_search.helpers.utilities.removeTags;

import au.com.agiledigital.idea_search.Status;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import au.com.agiledigital.idea_search.macros.transport.StatusContainer;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

/**
 * Velocity template renderer helper for Structured Fields. Transforms the body into a usable format
 * for the template based off the category
 */
public class StructureFieldRenderHelper {

  public static String render(StructuredCategory category, String body ) {
    return render(category, body, true, null);
  }

  public static String render(
    StructuredCategory category, String body, boolean heading, XhtmlContent xhtmlContent) {
    Map<String, Object> context = new HashMap<>();

    context.put("heading", heading);

    switch (category) {
      case TECHNOLOGIES:
        /** Splits the comma seperated string into a list and replaces all html tags */
        context.put("payload", splitTrimToSet(body, ",").stream().map(tech -> removeTags(tech)));
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
          } catch (XMLStreamException e) {
            e.printStackTrace();
          } catch (XhtmlException e) {
            e.printStackTrace();
          }
        }

        context.put("payload", bodyConverted);
        break;
      case STATUS:
        context.put("payload", new StatusContainer(Status.getStatusFromReference(body)));
        break;
    }

    return VelocityUtils.getRenderedTemplate("vm/" + category.getTemplate(), context);
  }
}
