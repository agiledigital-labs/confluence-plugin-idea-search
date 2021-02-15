package au.com.agiledigital.idea_search.dao;

import au.com.agiledigital.idea_search.model.FedexSchema;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fedex Idea Dao
 */
@Component
public class FedexSchemaDao {

  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoIdeaBlueprint> AO_IDEA_BLUEPRINT_TYPE = AoIdeaBlueprint.class;
  private static final Class<AoSchema> AO_IDEA_SCHEMA = AoSchema.class;

  @ComponentImport
  private final UserAccessor userAccessor;

  @Autowired
  public FedexSchemaDao(ActiveObjects ao, UserAccessor userAccessor) {
    this.ao = ao;
    this.userAccessor = userAccessor;
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

    return this.asSchema(aoSchema);
  }

  /**
   * Retrieve one schema from the database by the global id
   *
   * @param id global id of the schema
   * @return FedexSchema object
   */
  public FedexSchema findOneSchema(long id) {
    AoSchema aoSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select().limit(1).where("GLOBAL_ID = ?", id))[0];

    return this.asSchema(aoSchema);
  }

  /**
   * Retrieve one schema from the database by the global id
   *
   * @return FedexSchema object
   */
  public FedexSchema findCurrentSchema() {
    AoSchema aoSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select().limit(1).order("GLOBAL_ID DESC"))[0];

    return this.asSchema(aoSchema);
  }

  /**
   * Find every schema in the database
   *
   * @return List<FedexSchema> every schema in the database.
   */
  public List<FedexSchema> findAllSchema() {
    AoSchema[] aoSchema = this.ao.find(AO_IDEA_SCHEMA, Query.select());

    return this.asListFedexSchema(aoSchema);
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @return List<FedexTechnology>
   */
  private List<FedexSchema> asListFedexSchema(AoSchema[] aoSchemas) {
    return Arrays.stream(aoSchemas)
      .map(this::asSchema)
      .collect(Collectors.toList());
  }

  /**
   * Prepare fedex active object with the data from a fedex idea
   *
   * @param aoSchema    active object
   * @param fedexSchema with data to be added to the active object
   */
  private void prepareAOSchema(AoSchema aoSchema, FedexSchema fedexSchema) {
    aoSchema.setSchema(fedexSchema.getSchema());
    aoSchema.setUiSchema(fedexSchema.getUiSchema());
    aoSchema.setIndexSchema(fedexSchema.getIndexSchema());
    aoSchema.setDescription(fedexSchema.getDescription());
    aoSchema.setName(fedexSchema.getName());
    aoSchema.setVersion(fedexSchema.getVersion());
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
}
