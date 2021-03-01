package au.com.agiledigital.structured_form.model;

import au.com.agiledigital.structured_form.helpers.Utilities.PossiblesIndexEnum;
import com.google.gson.JsonElement;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Transport model to hold the index values
 */
public class FormIndex {

  private final Object value;
  private final Integer indexNumber;
  @Nonnull
  private final PossiblesIndexEnum type;
  private final String key;

  /**
   * Construct index where type is passed in as a string
   *
   * @param value of the index item
   * @param indexNumber assigned number of the index
   * @param type string, number, or boolean type of index
   * @param key from the json schema
   */
  public FormIndex(Object value, @Nonnull JsonElement indexNumber, @Nonnull String type, String key) {
    this.key = key;
    this.value = value;
    switch (type) {
      case "NUMBER":
        this.type = PossiblesIndexEnum.NUMBER;
        this.indexNumber = indexNumber.getAsInt();
        break;
      case "STATIC":
        this.type = PossiblesIndexEnum.STATIC;
        this.indexNumber = Integer.MIN_VALUE;
        break;
      case "BOOLEAN":
        this.type = PossiblesIndexEnum.BOOLEAN;
        this.indexNumber = indexNumber.getAsInt();
        break;
      default:
        this.type = PossiblesIndexEnum.STRING;
        this.indexNumber = indexNumber.getAsInt();
        break;
    }

  }

  /**
   * Construct the index when no index number is known
   *
   * @param value of the index item
   * @param type string, number, or boolean type of index
   * @param key from the json schema
   */
  public FormIndex(Object value, @Nonnull String type, String key) {
    this.indexNumber = Integer.MIN_VALUE;
    this.value = value;
    this.key = key;

    switch (type) {
      case "NUMBER":
        this.type = PossiblesIndexEnum.NUMBER;
        break;
      case "STATIC":
        this.type = PossiblesIndexEnum.STATIC;
        break;
      case "BOOLEAN":
        this.type = PossiblesIndexEnum.BOOLEAN;
        break;
      default:
        this.type = PossiblesIndexEnum.STRING;
        break;
    }

  }


  /**
   * Check if value is a string
   *
   * @return if the index is a string
   */
  public boolean isString() {
    return this.type == PossiblesIndexEnum.STRING || this.type == PossiblesIndexEnum.STATIC;
  }

  /**
   * Check if value is a number
   * @return if the index is a number
   */
  public boolean isNumber() {
    return this.type == PossiblesIndexEnum.NUMBER;
  }

  /**
   * Check if value is a boolean
   * @return if the index is a boolean
   */
  public boolean isBoolean() {
    return this.type == PossiblesIndexEnum.BOOLEAN;
  }

  /**
   * Get the values of the index as a map
   *
   * @return the index as a map
   */
  @Nonnull
  public Map<String, Object> getAsMap() {
    Map<String, Object> indexHashMap = new HashMap<>();

    indexHashMap.put("index", this.indexNumber);

    indexHashMap.put("value", this.getValue());

    indexHashMap.put("type", this.type.toString().toLowerCase());

    indexHashMap.put("key", this.key);

    return indexHashMap;
  }

  /**
   * Get the value of the index
   * @return the value in the type, ie string, nubmer, or boolean
   */
  public Object getValue() {
    return this.value;
  }

  /**
   * Get the number of the indexes number ie, 0-4
   * @return int from 0 to 4
   */
  public Integer getIndexNumber() {
    return this.indexNumber;
  }
}
