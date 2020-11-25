package au.com.agiledigital.idea_search.blueprints;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import java.util.Map;

/**
 * Context provider for Idea index blueprint
 */
public class IndexPageContextProvider extends AbstractBlueprintContextProvider {

  /**
   * Transforms the context during the creation process
   *
   * @param blueprintContext Contains the keys [spaceKey, pageFromTemplateTitle, analyticsKey,
   *                         blueprintKey, createFromTemplateLabel, indexKey, templateLabel,
   *                         blueprintId]
   * @return Context
   */
  @Override
  protected BlueprintContext updateBlueprintContext(
    BlueprintContext blueprintContext
  ) {
    return blueprintContext;
  }
}
