package au.com.agiledigital.idea_search.blueprints;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BlueprintPageContextProvider extends AbstractBlueprintContextProvider {

  private final List<KeyProperty> defaults = Arrays.asList(
    new KeyProperty(
      Parameter.IDEA_TITLE.reference,
      "I totally forgot to put one in the form"
    ),
    new KeyProperty(
      Parameter.IDEA_DESCRIPTION.reference,
      "It is awesome, how could it not be"
    ),
    new KeyProperty(
      Parameter.IDEA_OWNER.reference,
      "@me",
      new Options().withDefault(true).withUser(true)
    ),
    new KeyProperty(Parameter.IDEA_TEAM.reference, "none set")
  );

  private final String templatePath = "vm/";

  /**
   * Renders a value based on a template using the options object as the determiner.
   *
   * @param property The value to be rendered with the associated options
   * @return the rendered value as xhtml
   */
  private String renderValue(KeyProperty property) {
    if (property.key.startsWith("v")) {
      StringBuilder builder = new StringBuilder(templatePath);

      if (property.options.isDefault) {
        builder.append("PlaceHolder");
      }

      if (property.options.isUser) {
        builder.append("User");
      }

      if (property.options.isStatus) {
        builder.append("Status");
      }

      if (builder.length() > templatePath.length()) {
        return VelocityUtils.getRenderedTemplate(
          builder.append(".vm").toString(),
          Collections.singletonMap("message", property)
        );
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
  protected BlueprintContext updateBlueprintContext(
    BlueprintContext blueprintContext
  ) {
    Map<String, Object> contextMap = blueprintContext.getMap();

    blueprintContext.setTitle(contextMap.get("vIdeaTitle").toString());

    // Goes through map and adds in default values and transforms pre-existing values
    contextMap
      .entrySet()
      .forEach(
        entry ->
          contextMap.compute(entry.getKey(),
            (key, value) ->
              value == null || (value instanceof String && ((String) value).length() == 0)
                ? defaults
                .stream()
                .filter(property -> property.key.equals(key))
                .findFirst()
                .orElse(
                  new KeyProperty(key, "Something went very wrong here",
                    setupOptions(key, new Options().withDefault(true))))
                : new KeyProperty(key, entry.getValue(),
                  setupOptions(key, new Options().withDefault(false))
                )
          ));

    contextMap
      .entrySet()
      .forEach(
        entry -> entry.setValue(
          renderValue((KeyProperty) entry.getValue()))
      );

    return blueprintContext;
  }
}
