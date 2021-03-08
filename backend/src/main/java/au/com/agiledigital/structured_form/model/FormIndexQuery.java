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

/**
 * Form query made down to the database using defined indices
 */
public class FormIndexQuery {

  private final List<?> queries;
  @Nonnull
  private final PossiblesIndexEnum type;
  private final Integer indexNumber;
  @Nonnull
  private final String queryParam;

  /**
   * Construct the query object for transport
   *
   * @param queryParam from the rest endpoint
   * @param queries as a comma separated string
   */
  public FormIndexQuery(@Nonnull String queryParam, String queries) {
    this.type = getPossibles(queryParam);
    this.queryParam = queryParam;
    switch (getPossibles(queryParam)) {
      case NUMBER:
        this.indexNumber = Integer.parseInt(StringUtils.substring(queryParam, queryParam.length() - 1), 10);
        this.queries = (Arrays.stream(StringUtils.split(queries, ","))
          .map(Double::parseDouble).collect(Collectors.toList()));
        break;
      case BOOLEAN:
        this.indexNumber = Integer.parseInt(StringUtils.substring(queryParam, queryParam.length() - 1), 10);
        this.queries = (Arrays.stream(StringUtils.split(queries, ","))
          .map(Boolean::parseBoolean).collect(Collectors.toList()));
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

  /**
   * Convert a string value of type into the enum
   * @param key type of index
   * @return enum of type.
   */
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

  /**
   * Get the suffix of the query, ie string, number, or boolean
   *
   * @return suffix of the query as a string.
   */
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

  /**
   * Get the number of individual queries made against the index
   *
   * @return int of the number of queries made.
   */
  private int getLength() {
    return this.queries.size();
  }

  /**
   * Get sql query where statement and array of object to passed in.
   *
   * @return values needed to the sql query to database
   */
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

  /**
   * Create where claus for sql search
   *
   * @param list strings to be appended with %
   * @return strings appended with %
   */
  @Nonnull
  private List<String> indexSearchParams(@Nonnull List<String> list) {
    return list.stream().map(search -> search + "%").collect(Collectors.toList());
  }

  /**
   * Get sql query where statement and array of object to passed in for
   * string queries
   *
   * @param objects list of queries to be converted
   * @return values needed to the sql query to database
   */
  @Nonnull
  private Pair<String, List<String>> getListStringFunction(@Nonnull List<String> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aString ->
      getQueryString()).toArray(), " OR ").toUpperCase() + ")", this.indexSearchParams(objects));
  }

  /**
   * Get sql query where statement and array of object to passed in for
   * double queries
   *
   * @param objects list of queries to be converted
   * @return values needed to the sql query to database
   */
  @Nonnull
  private Pair<String, List<String>> getListDoubleFunction(@Nonnull List<Double> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aDouble ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(aDouble -> String.valueOf(aDouble.intValue())).collect(Collectors.toList())));
  }

  /**
   * Get sql query where statement and array of object to passed in for
   * boolean queries
   *
   * @param objects list of queries to be converted
   * @return values needed to the sql query to database
   */
  @Nonnull
  private Pair<String, List<String>> getListBooleanFunction(@Nonnull List<Boolean> objects) {
    return Pair.of("(" + StringUtils.join(objects.stream().map(aBoolean ->
        getQueryString()).toArray(), " OR ").toUpperCase() + ")",
      this.indexSearchParams(objects.stream().map(Object::toString).collect(Collectors.toList())));
  }

  /**
   * Uppercase string and append 'like ?'
   *
   * @return converted string
   */
  @Nonnull
  private String getQueryString() {
    return StringUtils.upperCase(this.getSuffix() + " like ?");
  }
}
