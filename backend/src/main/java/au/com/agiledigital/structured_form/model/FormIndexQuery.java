package au.com.agiledigital.structured_form.model;

import au.com.agiledigital.structured_form.helpers.Utilities.PossiblesIndexEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static au.com.agiledigital.structured_form.helpers.Utilities.PossiblesIndexEnum.*;

public class FormIndexQuery {


  private final List<?> queries;
  @Nonnull
  private final PossiblesIndexEnum type;
  private final Integer indexNumber;
  @Nonnull
  private final String queryParam;


  public FormIndexQuery(@Nonnull String queryParam, String queries) {
    this.type = getPossibles(queryParam);
    this.queryParam = queryParam;
    switch (getPossibles(queryParam)) {
      case NUMBER:
        this.indexNumber = Integer.parseInt(StringUtils.substring(queryParam, queryParam.length() - 1), 10);
        this.queries = (Arrays.stream(StringUtils.split(queries, ",")).map(Double::parseDouble).collect(Collectors.toList()));
        break;
      case BOOLEAN:
        this.indexNumber = Integer.parseInt(StringUtils.substring(queryParam, queryParam.length() - 1), 10);
        this.queries = (Arrays.stream(StringUtils.split(queries, ",")).map(Boolean::parseBoolean).collect(Collectors.toList()));
        break;
      case STATIC:
        this.indexNumber = Integer.MIN_VALUE;
        this.queries = (Arrays.asList((StringUtils.split(queries, ","))));
        break;
      default:
        this.queries = (Arrays.asList((StringUtils.split(queries, ","))));
        this.indexNumber = Integer.parseInt(StringUtils.substring(queryParam, queryParam.length() - 1), 10);
    }
  }

  @Nonnull
  private PossiblesIndexEnum getPossibles(String key) {
    if (StringUtils.startsWith(key, "number")) {
      return NUMBER;
    } else if (StringUtils.startsWith(key, "string")) {
      return STRING;
    } else if (StringUtils.startsWith(key, "boolean")) {
      return BOOLEAN;
    } else {
      return STATIC;
    }
  }

  @Nonnull
  private String getSuffix() {
    switch (type) {
      case BOOLEAN:
        return "index_boolean" + this.indexNumber;
      case STATIC:
        return this.queryParam;
      case NUMBER:
        return "index_number" + this.indexNumber;
      default:
        return "index_string" + this.indexNumber;
    }
  }

  private int getLength() {
    return this.queries.size();
  }

  @Nonnull
  public Pair<String, List<String>> getQuery() {
    if (this.queries != null && this.getLength() > 0) {
      switch (type) {
        case BOOLEAN:
          return this.getListBooleanFunction((List<Boolean>) this.queries);
        case NUMBER:
          return this.getListDoubleFunction((List<Double>) this.queries);
        default:
          return this.getListStringFunction((List<String>) this.queries);
      }

    } else {
      return Pair.of("", new ArrayList<>());
    }
  }

  @Nonnull
  private List<String> indexSearchParams(@Nonnull List<String> list) {
    return list.stream().map(search -> search + "%").collect(Collectors.toList());
  }

  @Nonnull
  private Pair<String, List<String>> getListStringFunction(@Nonnull List<String> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aString ->
      getQueryString()).toArray(), " OR ").toUpperCase() + ")", this.indexSearchParams(objects));
  }

  @Nonnull
  private Pair<String, List<String>> getListDoubleFunction(@Nonnull List<Double> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aDouble ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(aDouble -> String.valueOf(aDouble.intValue())).collect(Collectors.toList())));
  }

  @Nonnull
  private Pair<String, List<String>> getListBooleanFunction(@Nonnull List<Boolean> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aBoolean ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(Object::toString).collect(Collectors.toList())));
  }


  @Nonnull
  private String getQueryString() {
    return StringUtils.upperCase(this.getSuffix() + " like ?");
  }
}
