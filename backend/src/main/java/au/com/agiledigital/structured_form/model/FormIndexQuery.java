package au.com.agiledigital.structured_form.model;

import com.atlassian.fugue.Either;

import java.util.HashMap;
import java.util.Map;

public class FormIndexQuery extends FormIndex {

  private Either<String, Double> value;
  private Integer indexNumber;

  public FormIndexQuery(String value, int indexNumber, boolean isNumber) {
    super( value,  indexNumber,  isNumber);
  }

}
