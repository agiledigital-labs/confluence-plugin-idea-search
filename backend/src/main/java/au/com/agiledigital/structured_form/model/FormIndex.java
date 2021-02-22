package au.com.agiledigital.structured_form.model;

import com.atlassian.fugue.Either;

import java.util.HashMap;
import java.util.Map;

public class FormIndex {

  private Either<String, Double> value;
  private Integer indexNumber;

  public FormIndex(String value, int indexNumber, boolean isNumber) {
    this.indexNumber = indexNumber;
    if (isNumber) {
      this.value = Either.right(Double.parseDouble(value));
    } else {
      this.value = Either.left(value);
    }
  }
  public FormIndex(int indexNumber) {
    this.indexNumber = indexNumber;
  }

  public FormIndex(String value,  int indexNumber) {
    this.indexNumber = indexNumber;

    this.value = Either.left(value);
  }

  public FormIndex(Double value, int indexNumber) {
    this.indexNumber = indexNumber;

    this.value = Either.right(value);
  }

  public String getValueAsString() {
    if (value.isLeft()) {
      return value.swap().getOrElse("");
    } else {
      return value.getOrElse(Double.NaN).toString();
    }
  }


  public void setValue(String value){
    this.value = Either.left(value);
  }

  public void setValue(Double value){
    this.value = Either.right(value);
  }

  public String getString() {
    return value.swap().getOrElse("");
  }
  public Double getNumber() {
    return value.getOrElse(Double.NaN);
  }
    public boolean isString() {
    return value.isLeft();
  }
  public boolean isNumber() {
    return value.isRight();
  }

  public Map getAsMap() {
    Map<String, java.io.Serializable> test = new HashMap<>();

    test.put("index", this.indexNumber);
    if(this.value.isRight()) {
      test.put("value", this.value.getOrElse(Double.NEGATIVE_INFINITY));
    } else {
      test.put("value", this.value.swap().getOrElse(""));
    }
    test.put("type", this.value.isRight() ? "number" : "string");

    return test;
  }

  public Integer getIndexNumber() {
    return indexNumber;
  }
}
