package au.com.agiledigital.structured_form.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormIndexQuery {

  private enum Possibles {STRING, NUMBER, BOOLEAN, STATIC}

  private List<?> queries;
  private Possibles type;
  private Integer indexNumber;
  private String queryParam;


  public FormIndexQuery(int indexNumber, Possibles possibles, String queries) {
    this.indexNumber = indexNumber;

    this.type = possibles;
    switch (possibles) {
      case NUMBER:
        this.queries = (Arrays.stream(StringUtils.split(queries, ",")).map(Double::parseDouble).collect(Collectors.toList()));
        break;
      case BOOLEAN:
        this.queries = (Arrays.stream(StringUtils.split(queries, ",")).map(Boolean::parseBoolean).collect(Collectors.toList()));
        break;
      default:
        this.queries = (Arrays.asList((StringUtils.split(queries, ",")).clone()));

    }
  }

  public FormIndexQuery(String queryParam, String queries) {
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
  private Possibles getPossibles(String key) {
    if (StringUtils.startsWith(key, "number")) {
      return Possibles.NUMBER;
    } else if (StringUtils.startsWith(key, "string")) {
      return Possibles.STRING;
    } else if (StringUtils.startsWith(key, "boolean")) {
      return Possibles.BOOLEAN;
    } else {
      return Possibles.STATIC;
    }
  }

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

  public Pair<String, List<String>> getQuery() {
    if (this.queries != null && this.getLength() > 0) {
      switch (type) {
        case BOOLEAN:
          return this.getListBooleanFunction(((List<Boolean>) this.queries));
        case NUMBER:
          return this.getListDoubleFunction(((List<Double>) this.queries));
        default:
          return this.getListStringFunction(((List<String>) this.queries));
      }

    } else {
      return Pair.of("", new ArrayList<>());
    }
  }

  @Nonnull
  private List<String> indexSearchParams(List<String> list) {
    return list.stream().map(r -> r + "%").collect(Collectors.toList());
  }

  @Nonnull
  private Pair<String, List<String>> getListStringFunction(List<String> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(ll ->
      getQueryString()).toArray(), " OR ").toUpperCase() + ")", this.indexSearchParams(objects));
  }

  @Nonnull
  private Pair<String, List<String>> getListDoubleFunction(List<Double> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(ll ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(v -> String.valueOf(v.intValue())).collect(Collectors.toList())));
  }

  @Nonnull
  private Pair<String, List<String>> getListBooleanFunction(List<Boolean> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(ll ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(Object::toString).collect(Collectors.toList())));
  }


  @Nonnull
  private String getQueryString() {
    return StringUtils.upperCase(this.getSuffix() + " like ?");
  }
}
