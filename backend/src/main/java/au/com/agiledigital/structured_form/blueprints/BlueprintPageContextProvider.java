package au.com.agiledigital.structured_form.blueprints;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

import java.util.Map;

/**
 * Context provider for Idea page blueprint
 */
public class BlueprintPageContextProvider extends AbstractBlueprintContextProvider {

  /**
   * Transformer of the blueprint context. This takes the parameters from the wizard and transforms
   * them to meet the requirements of the application
   *
   * @param blueprintContext Context provided by Confluence
   * @return Update context
   */
  @Override
  protected BlueprintContext updateBlueprintContext(BlueprintContext blueprintContext) {
    Map<String, Object> contextMap = blueprintContext.getMap();

    blueprintContext.setTitle(contextMap.get("vIdeaTitle").toString());


    blueprintContext.put("blueprintId", blueprintContext.getBlueprintId());

    return blueprintContext;
  }
}
