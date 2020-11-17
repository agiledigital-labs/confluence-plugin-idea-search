package au.com.agiledigital.idea_search.macros;

import static au.com.agiledigital.idea_search.macros.StructureFieldRenderHelper.render;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MacroRepresentation {
  private final Node node;
  private final String macroName;
  private final StructuredCategory category;
  private String value;
  private String renderedValue;

  public MacroRepresentation(Node node, StructuredCategory category) {
    this.node = node;

    NamedNodeMap attributes = node.getAttributes();
    macroName = attributes.getNamedItem("ac:name").getNodeValue();
    this.category = category;

    Node child = node.getFirstChild();
    do {

      if (child instanceof Element && child.getNodeName().equals("ac:rich-text-body")) {
        value = child.getTextContent();

        renderedValue = render(category, value, false);
        break;
      }
    } while ((child = child.getNextSibling()) != null);
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
