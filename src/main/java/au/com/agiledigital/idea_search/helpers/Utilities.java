package au.com.agiledigital.idea_search.helpers;

import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSSerializer;

public class Utilities {

  private Utilities() {throw new IllegalStateException("Utility class"); }

  /**
   * Remove tags (\<tagName\>\<\/tagName\>) and give only wrapped text
   *
   * @param rawData the string to be stripped off tags
   * @return string without any tags wrapping it
   */
  public static String removeTags(String rawData) {
    return rawData.replaceAll("\\<[^>]+>\\>", "");
  }




  public static MacroRepresentation getMacroRepresentation(NodeList macros, StructuredCategory category, LSSerializer serializer, XhtmlContent xhtmlContent) {
    for (int i = 0; i < macros.getLength(); i++) {
      Node node = macros.item(i);

      String nodeName = node.getAttributes().getNamedItem("ac:name").getNodeValue();
      if (nodeName.equals("idea-structured-field") || nodeName.equals("Blueprint Id Storage")) {
        Node child = node.getFirstChild();
        do {
          if (child instanceof Element
            && child.getNodeName().equals("ac:parameter")
            && child.getTextContent().equals(category.getKey())) {
            return new MacroRepresentation(node, category, serializer, xhtmlContent);
          }
        } while ((child = child.getNextSibling()) != null);
      }
    }

    return null;
  }

}
