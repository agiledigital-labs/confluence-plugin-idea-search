package au.com.agiledigital.structured_form.model;

import com.atlassian.fugue.Either;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class FormIndex {
  public enum Possibles {STRING, NUMBER, BOOLEAN, STATIC}

  private Object value;
  private Integer indexNumber;
  private Possibles type;
  private String key;

  public FormIndex(String value, int indexNumber, Possibles type, String key) {
    this.indexNumber = indexNumber;
this.key = key;
    this.type = type;
    this.value = value;

  }
  public FormIndex(Object value, JsonElement indexNumber, String type, String key) {
    this.key = key;

    switch(type){
      case "NUMBER":
        this.type = Possibles.NUMBER;
    this.indexNumber = indexNumber.getAsInt();
        break;
      case "STATIC":
        this.type = Possibles.STATIC;
        this.indexNumber = Integer.MIN_VALUE;
        break;
      case "BOOLEAN":
        this.type = Possibles.BOOLEAN;
        this.indexNumber = indexNumber.getAsInt();
        break;
      default:
        this.type = Possibles.STRING;
        this.indexNumber = indexNumber.getAsInt();
        break;
    }
    this.value = value;

  }
  public FormIndex(Object value,  String type, String key) {
    this.indexNumber = Integer.MIN_VALUE;
    this.value = value;
    this.key = key;

    switch(type){
      case "NUMBER":
        this.type = Possibles.NUMBER;
        break;
      case "STATIC":
        this.type = Possibles.STATIC;
        this.indexNumber = Integer.MIN_VALUE;
        break;
      case "BOOLEAN":
        this.type = Possibles.BOOLEAN;
        break;
      default:
        this.type = Possibles.STRING;
        break;
    }

  }


  public String getValueAsString() {
    switch (this.type) {
      case NUMBER:
        return ((Double) this.value).toString();
      case BOOLEAN:
        return ((Boolean) this.value).toString();
      default:
        return ((String) this.value);

    }
  }


  public void setValue(String value) {
    this.value = Either.left(value);
  }

  public void setValue(Double value) {
    this.value = Either.right(value);
  }


  public boolean isString() {
    return this.type == Possibles.STRING || this.type == Possibles.STATIC;
  }

  public boolean isNumber() {
    return this.type == Possibles.NUMBER;
  }

  public boolean isBoolean() {
    return this.type == Possibles.BOOLEAN;
  }

  public Map getAsMap() {
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
