package au.com.agiledigital.idea_search.blueprints;

public class KeyProperty {

  public String key;
  public Object value;
  public Options options;

  /**
   * Creates a key property which is consumed during the context transformation process. Will set
   * option isDefault to true.
   *
   * @param key   property key used in the context
   * @param value property value used in the context
   */
  public KeyProperty(String key, Object value) {
    this(key, value, new Options().withDefault(true));
  }

  /**
   * Creates a key property which is consumed during the context transformation process.
   *
   * @param key     property key used in the context
   * @param value   property value used in the context
   * @param options options to further specify the metadata for the value
   */
  public KeyProperty(String key, Object value, Options options) {
    this.key = key;
    this.value = value;
    this.options = options;
  }

  public Options getOptions() {
    return options;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return ("KeyProperty{"
      + "key='"
      + key
      + '\''
      + ", value='"
      + value
      + "', options="
      + options
      + '}');
  }
}
