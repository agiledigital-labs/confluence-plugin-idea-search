package au.com.agiledigital.structured_form.model;

import au.com.agiledigital.structured_form.helpers.Utilities.PossiblesIndexEnum;
import com.atlassian.fugue.Either;
import com.google.gson.JsonElement;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class FormIndex {

  private Object value;
  private final Integer indexNumber;
  @Nonnull
  private final PossiblesIndexEnum type;
  private final String key;

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


  public void setValue(@Nonnull String value) {
    this.value = Either.left(value);
  }

  public void setValue(@Nonnull Double value) {
    this.value = Either.right(value);
  }


  public boolean isString() {
    return this.type == PossiblesIndexEnum.STRING || this.type == PossiblesIndexEnum.STATIC;
  }

  public boolean isNumber() {
    return this.type == PossiblesIndexEnum.NUMBER;
  }

  public boolean isBoolean() {
    return this.type == PossiblesIndexEnum.BOOLEAN;
  }

  @Nonnull
  public Map<String, Object> getAsMap() {
    Map<String, Object> test = new HashMap<>();

    test.put("index", this.indexNumber);

    test.put("value", this.getValue());

    test.put("type", this.type.toString().toLowerCase());

    test.put("key", this.key);

    return test;
  }

  public Object getValue() {
    return this.value;
  }

  public Integer getIndexNumber() {
    return this.indexNumber;
  }
}
