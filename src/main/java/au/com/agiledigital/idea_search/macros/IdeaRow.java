package au.com.agiledigital.idea_search.macros;

public class IdeaRow {
  public String title;
  public MacroRepresentation technologies;
  public String url;

  public String getTitle() {
    return title;
  }

  public MacroRepresentation getTechnologies() {
    return technologies;
  }

  public String getUrl() {
    return url;
  }
}