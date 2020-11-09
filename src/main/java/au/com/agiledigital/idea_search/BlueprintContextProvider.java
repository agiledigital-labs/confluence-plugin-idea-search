package au.com.agiledigital.idea_search;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BlueprintContextProvider extends AbstractBlueprintContextProvider {

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
   * Renders a value based on a template using the options object as the
   * determiner.
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
   * Determines if a value in a map is a lossy false (null or string with a
   * length of 0). If the evaluated value is true the left function will
   * run, if false the right function will run.
   *
   * @param map   the map containing the keys and values
   * @param key   key to evaluate
   * @param left  Function to run if the lossy check is true
   * @param right Function to run if the lossy check is false
   */
  private <K, V> void ifElseComputeLossyFalse(
    Map<K, V> map,
    K key,
    Function<K, V> left,
    Function<K, V> right
  ) {
    V value = map.get(key);
    V newValue =
      (
        value == null ||
          (value instanceof String && ((String) value).length() == 0)
          ? left
          : right
      ).apply(key);

    map.put(key, newValue);
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
   * Transformer of the blueprint context. This takes the parameters from the
   * wizard and transforms them to meet the requirements of the application
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

    contextMap
      .entrySet()
      .forEach(
        entry ->
          ifElseComputeLossyFalse(
            contextMap,
            entry.getKey(),
            k ->
              defaults
                .stream()
                .filter(property -> property.key.equals(k))
                .findFirst()
                .orElse(
                  new KeyProperty(
                    k,
                    "Something went very wrong here",
                    setupOptions(k, new Options().withDefault(true))
                  )
                ),
            k ->
              new KeyProperty(
                k,
                entry.getValue(),
                setupOptions(k, new Options().withDefault(false))
              )
          )
      );

    contextMap.entrySet().forEach(System.out::println);

    contextMap
      .entrySet()
      .forEach(
        entry -> entry.setValue(renderValue((KeyProperty) entry.getValue()))
      );

    return blueprintContext;
  }
}
