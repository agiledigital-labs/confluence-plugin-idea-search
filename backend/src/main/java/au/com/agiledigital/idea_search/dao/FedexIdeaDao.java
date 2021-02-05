package au.com.agiledigital.idea_search.dao;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.model.FedexTechnology;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Fedex Idea Dao
 */
@Component
public class FedexIdeaDao {

  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoFedexIdea> AO_FEDEX_IDEA_TYPE = AoFedexIdea.class;
  private static final Class<AoFedexTechnology> AO_FEDEX_TECHNOLOGY_TYPE = AoFedexTechnology.class;
  private static final Class<AoIdeaBlueprint> AO_IDEA_BLUEPRINT_TYPE = AoIdeaBlueprint.class;
  private static final Class<AoSchema> AO_IDEA_SCHEMA = AoSchema.class;

  @ComponentImport
  private final UserAccessor userAccessor;

  @Autowired
  public FedexIdeaDao(ActiveObjects ao, UserAccessor userAccessor) {
    this.ao = ao;
    this.userAccessor = userAccessor;
  }

  /**
   * Create new entry to represent the a FedexIdea
   *
   * @param fedexIdea FedexIdea model object
   * @return FedexIdea object created in data store
   */
  public FedexIdea createIdea(FedexIdea fedexIdea) {
    AoFedexIdea aoFedexIdea = this.ao.create(AO_FEDEX_IDEA_TYPE);

    this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

    // Save the changes to the active object
    aoFedexIdea.save();
    // Set the relation of the technology to the idea and save
//    setTechnologies(getAoFedexTechnologies(fedexIdea), aoFedexIdea);

    return this.asFedexIdea(aoFedexIdea);
  }

  /**
   * Create new entry to represent the a FedexIdea
   *
   * @param fedexSchema FedexIdea model object
   * @return FedexSchema object created in data store
   */
  public FedexSchema createSchema(FedexSchema fedexSchema) {
    AoSchema aoSchema = this.ao.create(AO_IDEA_SCHEMA);

    this.prepareAOSchema(aoSchema, fedexSchema);

    // Save the changes to the active object
    aoSchema.save();

    String schema = aoSchema.getSchema();
    String uiSchema = aoSchema.getUiSchema();

    return this.asSchema(aoSchema);
  }

  /**
   * Get the id of plugin blueprint
   *
   * @return blueprintId or an empty string if none found
   */
  public String getBlueprintId() {
    AoIdeaBlueprint[] blueprints = this.ao.find(AO_IDEA_BLUEPRINT_TYPE, Query.select());
    return blueprints.length == 0 ? "" : blueprints[0].getBlueprintId();
  }

  /**
   * Set the blueprint id if not current
   *
   * @param blueprintId the supplied blueprint id
   */
  public void setBlueprintId(String blueprintId) {
    AoIdeaBlueprint[] blueprint = this.ao
      .find(AO_IDEA_BLUEPRINT_TYPE, Query.select().where("BLUEPRINT_ID = ?", blueprintId));

    // Check if there is already a blueprint
    if (blueprint.length != 0) {
      // If the existing blueprint id does not match with supplied one,
      // then delete it and create a new one with the supplied id,
      // otherwise keep blueprint id unchanged.
      if (!blueprint[0].getBlueprintId().equals(blueprintId)) {
        this.ao.delete(blueprint);
        createBlueprintIdEntry(blueprintId);
      }
      // If no blueprint id is found,
      // then create a new blueprint id with the supplied one.
    } else {
      createBlueprintIdEntry(blueprintId);
    }
  }

  // Creates a blueprintId in the ao database with supplied blueprint id
  private void createBlueprintIdEntry(String blueprintId) {
    AoIdeaBlueprint newBlueprint = this.ao.create(AO_IDEA_BLUEPRINT_TYPE);
    newBlueprint.setBlueprintId(blueprintId);
    newBlueprint.save();
  }

  /**
   * Update a FedexIdea by the page content id
   *
   * @param fedexIdea New FedexIdea for data store
   * @param contentId of the page containing the idea
   * @return FedexIdea saved to the data store
   */
  public FedexIdea upsertByContentId(FedexIdea fedexIdea, long contentId) {
    AoFedexIdea[] aoFedexIdeas =
      this.ao.find(
        AO_FEDEX_IDEA_TYPE,
        Query.select().where("CONTENT_ID = ?", contentId)
      );

    boolean newIdea = aoFedexIdeas.length == 0;

    // If the title of the page already exists the create event fails and a page update event is fired on a successful save.
    // If this happens the aoFedexIdea will not exist in the data store and will need to be created.
    AoFedexIdea aoFedexIdea = newIdea
      ? this.ao.create(AO_FEDEX_IDEA_TYPE)
      : aoFedexIdeas[0];

    // If the idea exists, remove the existing technologies so they can be updated
    if (!newIdea) {
      List<AoFedexTechnology> aoFedexTechnology = Arrays.asList(
        aoFedexIdea.getTechnologies()
      );
      // Deletes the technology item from the table
      aoFedexTechnology.forEach(this.ao::delete);
    }
    if (fedexIdea.getTechnologies() != null) {

      List<AoFedexTechnology> aoTechList = getAoFedexTechnologies(fedexIdea);
      setTechnologies(aoTechList, aoFedexIdea);

    }
    this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

    aoFedexIdea.save();

    return this.asFedexIdea(aoFedexIdea);
  }

  /**
   * Update a FedexIdea by the page content id
   *
   * @param contentId of the page containing the idea
   * @return FedexIdea saved to the data store
   */
  public FedexIdea getByContentId(long contentId) {

    List<AoFedexIdea> test = Arrays.stream(this.ao.find(
      AO_FEDEX_IDEA_TYPE,
      Query.select().where("CONTENT_ID = ?", contentId)
    )).collect(Collectors.toList());

    FedexIdea other = test.stream().map(this::asFedexIdea).collect(Collectors.toList()).get(0);

    return other;
  }

  /**
   * Extract technologies from FedexIdea and create an active object for each
   *
   * @param fedexIdea from model
   * @return List<AoFedexTechnology> unsaved.
   */
  private List<AoFedexTechnology> getAoFedexTechnologies(FedexIdea fedexIdea) {
    return fedexIdea.getTechnologies().stream()
      .map(tech -> this.prepareAOFedexTechnology(tech.getTechnology()))
      .collect(Collectors.toList());
  }

  // See comment on
  // https://community.atlassian.com/t5/Jira-questions/ActiveObjects-jira/qaq-p/354375.
  // This means that the setter is done on the Recipient entity(setting the Filter).
  private void setTechnologies(
    List<AoFedexTechnology> aoFedexTechnologies, AoFedexIdea aoFedexIdea) {
    aoFedexTechnologies.forEach(
      techItem -> {
        techItem.setIdea(aoFedexIdea);
        techItem.save();
      });
  }

  /**
   * List all fedex idea in the data store
   *
   * @return a list of all available FedexIdea
   */
  public List<FedexIdea> findAll(String title, String description, String status, String owner) {
     Query query = Query
       .select()
       .where("lower(description) LIKE ? AND lower(title) LIKE ? AND lower(status) LIKE ? AND lower(owner) LIKE ?",
         description.toLowerCase() + "%", title.toLowerCase() + "%", status.toLowerCase() + "%", owner.toLowerCase() + "%");

    AoFedexIdea[] aoFedexIdeas = this.ao.find(AO_FEDEX_IDEA_TYPE, query);
    return this.asListFedexIdea(aoFedexIdeas);
  }

  /**
   * List all fedex idea technology in the data store
   *
   * @return a list of all available FedexTechnology
   */
  public List<FedexTechnology> findAllTech() {
    AoFedexTechnology[] aoFedexTechnologies = this.ao
      .find(AO_FEDEX_TECHNOLOGY_TYPE, Query.select());
    return this.asListFedexTechnology(aoFedexTechnologies);
  }

  public FedexSchema findOneSchema(long id) {
    AoSchema aoSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select().where("GLOBAL_ID = ?", id))[0];

    return this.asSchema(aoSchema);
  }

  private static String DEFAULT_SCHEMA = "{\n" +
    "  \"title\": \"A fedex Idea or puzzle\",\n" +
    "  \"description\": \"Something interesting that could be worked on either in downtime or a fedex day\",\n"
    +
    "  \"type\": \"object\",\n" +
    "  \"required\": [\n" +
    "    \"ideaTitle\"\n" +
    "  ],\n" +
    "  \"properties\": {\n" +
    "    \"ideaTitle\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea Title (or how it should be know)\",\n" +
    "      \"default\": \"Other things\"\n" +
    "    },\n" +
    "    \"description\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Description\"\n" +
    "    },\n" +
    "    \"owner\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Idea owner\"\n" +
    "    },\n" +
    "    \"status\":{\n" +
    "          \"type\": \"string\",\n" +
    "          \"enum\": [\n" +
    "            \"new\",\n" +
    "            \"inProgress\",\n" +
    "            \"completed\",\n" +
    "            \"abandoned\"\n" +
    "          ],\"enumNames\": [\"New\", \"In Progress\", \"Completed\", \"Abandoned\"],\n" +
    "          \"default\": \"New\"\n" +
    "        \n" +
    "    },\n" +
    "        \"team\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The team\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "       \"technologies\": {\n" +
    "      \"type\": \"array\",\n" +
    "      \"title\": \"The tech\",\n" +
    "      \"items\":{\n" +
    "        \"type\": \"string\"\n" +
    "      }\n" +
    "    },\n" +
    "           \"links\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to resources for this idea\"\n" +
    "    },\n" +
    "           \"tickets\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Links to issues or tickets that track this\"\n" +
    "    },\n" +
    "           \"talks\": {\n" +
    "      \"type\": \"string\",\n" +
    "      \"title\": \"Presentations on the idea\"\n" +
    "    }\n" +
    "  }\n" +
    "}";

  public AoSchema findRawOneSchema(long id) {

    AoSchema[] listSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select().where("GLOBAL_ID = ?", id));
    if (listSchema.length > 0) {
      return listSchema[0];
    }

    AoSchema aoSchema = this.ao.create(AO_IDEA_SCHEMA);

    aoSchema.setSchema(DEFAULT_SCHEMA);

    aoSchema.save();

    return aoSchema;

  }

  public List<FedexSchema> findAllSchema() {
    AoSchema[] aoSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select());

    return this.asListFedexSchema(aoSchema);
  }


  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFedexIdeas list of active object ideas to be converted to a list of the model
   *                     FedexIdea
   * @return List<FedexIdea>
   */
  private List<FedexIdea> asListFedexIdea(AoFedexIdea[] aoFedexIdeas) {
    return Arrays.stream(aoFedexIdeas).map(this::asFedexIdea).collect(Collectors.toList());
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFedexTechnologies AoFedexTechnology[]
   * @return List<FedexTechnology>
   */
  private List<FedexTechnology> asListFedexTechnology(AoFedexTechnology[] aoFedexTechnologies) {
    return Arrays.stream(aoFedexTechnologies)
      .map(this::asFedexTechnology)
      .collect(Collectors.toList());
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoSchemas AoFedexTechnology[]
   * @return List<FedexTechnology>
   */
  private List<FedexSchema> asListFedexSchema(AoSchema[] aoSchemas) {
    return Arrays.stream(aoSchemas)
      .map(this::asSchema)
      .collect(Collectors.toList());
  }

  /**
   * Convert a user name string into a user key ID
   *
   * @param userName taking the confluence action
   * @return string of the user key ID
   */
  private String getUserKey(String userName) {
    if (userName == null) {
      return null;
    } else {
      ConfluenceUser user = this.userAccessor.getUserByName(userName);
      return user == null ? null : user.getKey().getStringValue();
    }
  }

  /**
   * Convert a user key ID to the users name
   *
   * @param userKey string of the user key id
   * @return userName string
   */
  private String getUsername(String userKey) {
    if (userKey != null) {
      ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
      return user == null ? null : user.getLowerName();
    }

    return null;
  }


  /**
   * Prepare technology string for saving as an an active object
   *
   * @param technology string
   * @return AoFedexTechnology object to be saved to the data store
   */
  private AoFedexTechnology prepareAOFedexTechnology(String technology) {
    AoFedexTechnology aoFedexTechnology = this.ao.create(AoFedexTechnology.class);
    aoFedexTechnology.setTechnology(technology == null ? "" : technology);
    return aoFedexTechnology;
  }

  /**
   * Prepare fedex active object with the data from a fedex idea
   *
   * @param aoFedexIdea active object
   * @param fedexIdea   with data to be added to the active object
   */
  private void prepareAOFedexIdea(AoFedexIdea aoFedexIdea, FedexIdea fedexIdea) {
    // TODO: Uncomment event and put actual values
    aoFedexIdea.setContentId(fedexIdea.getContentId());
    aoFedexIdea.setCreatorUserKey(this.getUserKey(fedexIdea.getCreator()));
    // aoFedexIdea.setOwner("admin");
    aoFedexIdea.setOwner(fedexIdea.getOwner());
    // aoFedexIdea.setStatus("inProgress");
    aoFedexIdea.setStatus(fedexIdea.getStatus());
    aoFedexIdea.setTitle(fedexIdea.getTitle());
    // aoFedexIdea.setDescription("Demo description");
    aoFedexIdea.setDescription(fedexIdea.getDescription());
  }

  /**
   * Prepare fedex active object with the data from a fedex idea
   *
   * @param aoSchema    active object
   * @param fedexSchema with data to be added to the active object
   */
  private void prepareAOSchema(AoSchema aoSchema, FedexSchema fedexSchema) {
    aoSchema.setSchema(fedexSchema.getSchema());
    aoSchema.setUiSchema(fedexSchema.getUiSchema());   //(this.getUserKey(fedexSchema.getUiSchema()));
    aoSchema.setIndexSchema(fedexSchema.getIndexSchema());
    aoSchema.setDescription(fedexSchema.getDescription());
    aoSchema.setName(fedexSchema.getName());
    aoSchema.setVersion(fedexSchema.getVersion());
  }

  /**
   * Convert fedex idea active object to a fedex idea model object
   *
   * @param aoFedexIdea active object to be converted
   * @return FedexIdea object
   */
  private FedexIdea asFedexIdea(AoFedexIdea aoFedexIdea) {
    return aoFedexIdea == null
      ? null
      : (new FedexIdea.Builder())
        .withGlobalId(aoFedexIdea.getGlobalId())
        .withTitle(aoFedexIdea.getTitle())
        .withOwner(aoFedexIdea.getOwner())
        .withContentId(aoFedexIdea.getContentId())
        .withTechnologies(asListFedexTechnology(aoFedexIdea.getTechnologies()))
        .withCreator(this.getUsername(aoFedexIdea.getCreatorUserKey()))
        .withDescription(aoFedexIdea.getDescription())
        .withStatus(aoFedexIdea.getStatus())
        .withFormData(aoFedexIdea.getFormData())
//        .withSchemaId(this.getSchemaFromIdea(aoFedexIdea.getContentId()))
        .build();
  }


  /**
   * Convert fedex idea active object to a fedex idea model object
   *
   * @param aoSchema active object to be converted
   * @return FedexSchema object
   */
  private FedexSchema asSchema(AoSchema aoSchema) {
    return aoSchema == null
      ? null
      : (new FedexSchema.Builder())
        .withGlobalId(aoSchema.getGlobalId())
        .withSchema(aoSchema.getSchema())
        .withUiSchema(aoSchema.getUiSchema())
        .withIndexSchema(aoSchema.getIndexSchema())
        .withName(aoSchema.getName())
        .withDescription(aoSchema.getDescription())
        .withVersion(aoSchema.getVersion())
        .build();
  }

  /**
   * Convert fedex technology active object to a fedex technology model object
   *
   * @param aoFedexTechnology active object to be converted
   * @return FedexTechnology object
   */
  private FedexTechnology asFedexTechnology(AoFedexTechnology aoFedexTechnology) {
    return aoFedexTechnology == null
      ? null
      : new FedexTechnology.Builder()
        .withGlobalId(aoFedexTechnology.getGlobalId())
        .withTechnology(aoFedexTechnology.getTechnology())
        .build();
  }

  /**
   * Creates list of distinct technologies
   *
   * @param aoFedexTechnologies from the dao query
   * @return List<TechnologyAPI> to be passed to the rest API
   */
  private List<TechnologyAPI> getDistinctTechnology(
    AoFedexTechnology[] aoFedexTechnologies
  ) {
    return Arrays
      .stream(aoFedexTechnologies)
      .distinct()
      .map(t -> new TechnologyAPI(t.getTechnology()))
      .collect(Collectors.toList());
  }

  /**
   * Collect a list of technologies in ascending order from the database. Filter technology list
   * from dao to avoid technology duplication.
   *
   * @return list of strings (technology names)
   */
  public List<TechnologyAPI> queryTechList() {
    Query query = Query.select("TECHNOLOGY").order("TECHNOLOGY ASC");
    AoFedexTechnology[] aoFedexTechnologies =
      this.ao.find(AO_FEDEX_TECHNOLOGY_TYPE, query);

    return getDistinctTechnology(
      aoFedexTechnologies
    );
  }

  /**
   * Collect a list of technologies in ascending order from the database. overloaded to take a
   * search string Filter technology list from dao to avoid technology duplication.
   *
   * @param searchString used to find technologies in the database
   * @return list of strings (technology names)
   */
  public List<TechnologyAPI> queryTechList(String searchString) {
    Query query = Query
      .select("TECHNOLOGY")
      .order("TECHNOLOGY ASC")
      .where("TECHNOLOGY like ?", searchString + "%");
    AoFedexTechnology[] aoFedexTechnologies =
      this.ao.find(AO_FEDEX_TECHNOLOGY_TYPE, query);

    return getDistinctTechnology(
      aoFedexTechnologies
    );
  }
}
