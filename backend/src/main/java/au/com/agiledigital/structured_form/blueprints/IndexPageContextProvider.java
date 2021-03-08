package au.com.agiledigital.structured_form.blueprints;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

/**
 * Context provider for Form schema index blueprint
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
  protected BlueprintContext updateBlueprintContext(BlueprintContext blueprintContext) {
    return blueprintContext;
  }
}
