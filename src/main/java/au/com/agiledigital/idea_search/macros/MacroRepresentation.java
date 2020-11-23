package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.helpers.StructureFieldRenderHelper.render;

import com.atlassian.confluence.xhtml.api.XhtmlContent;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSSerializer;

/**
 * Represents a Structured Field Macro converted from the Confluence Storage format
 */
public class MacroRepresentation {

  private final Node node;
  private final String macroName;
  private final StructuredCategory category;
  private final LSSerializer serializer;
  private String value;
  private String renderedValue;

  public MacroRepresentation(Node node,
    StructuredCategory category,
    LSSerializer serializer,
    XhtmlContent xhtmlContent) {
    this.node = node;
    this.serializer = serializer;

    NamedNodeMap attributes = node.getAttributes();
    macroName = attributes.getNamedItem("ac:name").getNodeValue();
    this.category = category;

    Node child = node.getFirstChild();
    do {

      if (child instanceof Element && child.getNodeName().equals("ac:rich-text-body")) {
        value = serialiseNode(child);
        renderedValue = render(category, value, false, xhtmlContent);
        break;
      }
    } while ((child = child.getNextSibling()) != null);
  }

  private String serialiseNode(Node node) {
    String serialised = serializer.writeToString(node);

    return serialised
      .substring(serialised.indexOf("body>") + 5, serialised.indexOf("</ac:rich"));
  }

  public Node getNode() {
    return node;
  }

  public String getMacroName() {
    return macroName;
  }

  public StructuredCategory getCategory() {
    return category;
  }

  public String getValue() {
    return value;
  }

  public String getRenderedValue() {
    return renderedValue;
  }
}
