package au.com.agiledigital.idea_search.macros.transport;

import au.com.agiledigital.idea_search.macros.MacroRepresentation;
import au.com.agiledigital.idea_search.macros.StructuredCategory;

/**
 * Container of Idea metadata to be used within a velocity template
 */
public class IdeaContainer {

  public String title;
  public String url;

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

  public void setMacroRepresentations(StructuredCategory category, MacroRepresentation macro) {
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
    }
  }
}