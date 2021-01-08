package au.com.agiledigital.idea_search.blueprints;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context provider for Idea page blueprint
 */
public class BlueprintPageContextProvider extends AbstractBlueprintContextProvider {

  public final List<KeyProperty> ideaFieldsDefaults =
    Arrays.asList(
      new KeyProperty(
        Parameter.IDEA_TITLE.getReference(), "I totally forgot to put one in the form"),
      new KeyProperty(
        Parameter.IDEA_DESCRIPTION.getReference(), "It is awesome, how could it not be"),
      new KeyProperty(
        Parameter.IDEA_OWNER.getReference(),
        "@me"
      ),
      new KeyProperty(Parameter.IDEA_TECHNOLOGY.getReference(), "Add your technologies"),
      new KeyProperty(Parameter.IDEA_TEAM.getReference(), "none set"));

  /**
   * Renders a value based on a template using the options object as the determiner.
   *
   * @param property The value to be rendered with the associated options
   * @return the rendered value as xhtml
   */
  private String renderValue(KeyProperty property) {
    if (property.key.startsWith("v")) {
      String templatePath = "vm/";
      StringBuilder builder = new StringBuilder(templatePath);

      if (property.options.isDefault) {
        builder.append("PlaceHolder");
      }

      if (property.options.isUser) {
        builder.append("User");

        property.value = (property.value.toString()).split(",");
      }

      if (property.options.isTechnology) {
        builder.append("Technology");
      }

      if (property.options.isStatus) {
        builder.append("Status");
      }

      if (builder.length() > templatePath.length()) {
        // Using a hashmap as Confluence may modify this map (Needs to be mutable otherwise error)
        HashMap<String, Object> context = new HashMap<>();
        context.put("message", property);

        return VelocityUtils.getRenderedTemplate(builder.append(".vm").toString(), context);
      }
    }

    return property.value.toString();
  }

  /**
   * Flags the options object with additional options based off the key
   *
   * @param key     Key of the context item
   * @param options options for the context item
   * @return The updated options
   */
  private <K extends String> Options setupOptions(K key, Options options) {
    Parameter parameter = Parameter.getParameterFromReference(key);

    if (parameter != null) {
      switch (parameter) {
        case IDEA_STATUS:
          options.withStatus(true);
          break;
        case IDEA_OWNER:
        case IDEA_TEAM:
          options.withUser(true);
          break;
        case IDEA_TECHNOLOGY:
          options.withTechnology(true);
          break;
        default:
          break;
      }
    }

    return options;
  }

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

    // Goes through map and adds in default values and transforms pre-existing values
    contextMap
      .entrySet()
      .forEach(
        entry ->
          contextMap.compute(
            entry.getKey(),
            (key, value) ->
              value == null || (value instanceof String && ((String) value).length() == 0)
                ? ideaFieldsDefaults.stream()
                .filter(property -> property.key.equals(key))
                .findFirst()
                .orElse(
                  new KeyProperty(
                    key,
                    "Something went very wrong here",
                    setupOptions(key, new Options().withDefault(true))))
                : new KeyProperty(
                  key,
                  entry.getValue(),
                  setupOptions(key, new Options().withDefault(false)))));

    contextMap
      .entrySet()
      .forEach(entry -> entry.setValue(renderValue((KeyProperty) entry.getValue())));

    contextMap.put("blueprintId", blueprintContext.getBlueprintId());

    return blueprintContext;
  }
}
