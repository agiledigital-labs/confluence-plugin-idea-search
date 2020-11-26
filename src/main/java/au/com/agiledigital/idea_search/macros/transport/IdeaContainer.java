package au.com.agiledigital.idea_search.macros.transport;

import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;
import org.w3c.dom.Node;

/**
 * Container of Idea metadata to be used within a velocity template
 */
public class IdeaContainer {

  public String title;
  public String url;
  public String blueprintId;

  public MacroRepresentation technologies;
  public MacroRepresentation description;
  public MacroRepresentation status;
  public MacroRepresentation owner;

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public MacroRepresentation getTechnologies() {
    return technologies;
  }

  public MacroRepresentation getDescription() {
    return description;
  }

  public MacroRepresentation getStatus() {
    return status;
  }

  public MacroRepresentation getOwner() {
    return owner;
  }

  public void setMacroRepresentations(
    StructuredCategory category,
    MacroRepresentation macro
  ) {
    switch (category) {
      case DESCRIPTION:
        description = macro;
        break;
      case TECHNOLOGIES:
        technologies = macro;
        break;
      case STATUS:
        status = macro;
        break;
      case OWNER:
        owner = macro;
        break;
      case BLUEPRINT_ID:
        if (macro != null) {
          Node child = macro.getNode().getFirstChild();
          do {
            if (
              child
                .getAttributes()
                .getNamedItem("ac:name")
                .getNodeValue()
                .equals("blueprintId")
            ) {
              blueprintId = child.getTextContent();
              break;
            }
          } while ((child = child.getNextSibling()) != null);
        }

        break;
    }
  }
}
