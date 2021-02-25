package au.com.agiledigital.structured_form.helpers;

/**
 * Supplies default schemas when there is no schema saved in the database.
 */
public class DefaultSchema {

  private DefaultSchema() { }

  /**
   * The default page schema, this will show up when trying to create a page
   * from this plugin without setting a custom schema.
   * Currently it is from markdown ADR: https://adr.github.io/madr/#the-template.
   */
  public static final String SCHEMA = "{\n"
    + "  \"title\": \"Architectural Decision Records form\",\n"
    + "  \"description\": \"Default schema (for capturing Architectural Decision Records) \",\n"
    + "  \"type\": \"object\",\n"
    + "  \"properties\": {\n"
    + "    \"status\": {\n"
    + "      \"type\": \"string\",\n" // NOSONAR string schema declaration can contain duplicates
    + "      \"title\": \"Status\",\n"
    + "      \"enum\": [\n"
    + "        \"proposed\",\n"
    + "        \"rejected\",\n"
    + "        \"accepted\",\n"
    + "        \"deprecated\",\n"
    + "        \"superseded\"\n"
    + "      ]\n"
    + "    },\n" // NOSONAR string schema declaration can contain duplicates
    + "    \"deciders\": {\n"
    + "      \"type\": \"string\",\n"
    + "      \"title\": \"Deciders\"\n"
    + "    },\n"
    + "    \"date\": {\n"
    + "      \"type\": \"string\",\n"
    + "      \"format\": \"date\",\n"
    + "      \"title\": \"Date\"\n"
    + "    },\n"
    + "    \"technicalStory\": {\n"
    + "      \"type\": \"string\",\n"
    + "      \"title\": \"Technical Story\"\n"
    + "    },\n"
    + "    \"problemStatement\": {\n"
    + "      \"type\": \"string\",\n"
    + "      \"title\": \"Problem Statement\"\n"
    + "    },\n"
    + "    \"listOfDecisionDrivers\": {\n"
    + "      \"type\": \"array\",\n" // NOSONAR string schema declaration can contain duplicates
    + "      \"title\": \"A list of decision drivers\",\n"
    + "      \"minItems\": 1,\n" // NOSONAR string schema declaration can contain duplicates
    + "      \"items\": {\n" // NOSONAR string schema declaration can contain duplicates
    + "        \"type\": \"string\"\n" // NOSONAR string schema declaration can contain duplicates
    + "      }\n" // NOSONAR string schema declaration can contain duplicates
    + "    },\n"
    + "    \"listOfConsideredOptions\": {\n"
    + "      \"type\": \"array\",\n"
    + "      \"title\": \"A list of considered options\",\n"
    + "      \"minItems\": 1,\n"
    + "      \"items\": {\n"
    + "        \"type\": \"string\"\n"
    + "      }\n"
    + "    },\n"
    + "    \"decisionOutcome\": {\n"
    + "      \"type\": \"string\",\n"
    + "      \"title\": \"Decision Outcome\"\n"
    + "    },\n"
    + "    \"listOfConsequences\": {\n"
    + "      \"type\": \"array\",\n"
    + "      \"title\": \"A list of consequences\",\n"
    + "      \"minItems\": 1,\n"
    + "      \"items\": {\n"
    + "        \"type\": \"string\"\n"
    + "      }\n"
    + "    },\n"
    + "    \"listOfPros\": {\n"
    + "      \"type\": \"array\",\n"
    + "      \"title\": \"A list of pros\",\n"
    + "      \"minItems\": 1,\n"
    + "      \"items\": {\n"
    + "        \"type\": \"string\"\n"
    + "      }\n"
    + "    },\n"
    + "    \"listOfCons\": {\n"
    + "      \"type\": \"array\",\n"
    + "      \"title\": \"A list of cons\",\n"
    + "      \"minItems\": 1,\n"
    + "      \"items\": {\n"
    + "        \"type\": \"string\"\n"
    + "      }\n"
    + "    },\n"
    + "    \"listOfLinks\": {\n"
    + "      \"type\": \"array\",\n"
    + "      \"title\": \"A list of links\",\n"
    + "      \"minItems\": 1,\n"
    + "      \"items\": {\n"
    + "        \"type\": \"string\"\n"
    + "      }\n"
    + "    }\n"
    + "  }\n"
    + "}";

  /**
   * Default schema for ui styling.
   */
  public static final String UI_SCHEMA = "{\n"
    + "  \"technicalStory\": {\n"
    + "    \"ui:widget\": \"textarea\"\n"
    + "  },\n"
    + "  \"problemStatement\": {\n"
    + "    \"ui:widget\": \"textarea\"\n"
    + "  }\n"
    + "}";

  /**
   * Default index fields for ADR schema.
   */
  public static final String INDEX_SCHEMA = "{\n"
    + "  \"index\": [\n"
    + "    {\n" // NOSONAR string schema declaration can contain duplicates
    + "      \"key\": \"status\",\n"
    + "      \"index\": 1,\n"
    + "      \"type\": \"string\"\n" // NOSONAR string schema declaration can contain duplicates
    + "    },\n"
    + "    {\n"
    + "      \"key\": \"date\",\n"
    + "      \"index\": 1,\n"
    + "      \"type\": \"string\"\n"
    + "    },\n"
    + "    {\n"
    + "      \"key\": \"deciders\",\n"
    + "      \"index\": 2,\n"
    + "      \"type\": \"string\"\n"
    + "    },\n"
    + "    {\n"
    + "      \"key\": \"decisionOutcome\",\n"
    + "      \"index\": 0,\n"
    + "      \"type\": \"string\"\n"
    + "    },\n"
    + "    {\n"
    + "      \"key\": \"title\",\n"
    + "      \"type\": \"static\"\n"
    + "    }\n"
    + "  ]\n"
    + "}";

}
