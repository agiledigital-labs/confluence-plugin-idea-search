package au.com.agiledigital.structured_form.dao;

import au.com.agiledigital.structured_form.model.FormSchema;
import au.com.agiledigital.structured_form.helpers.DefaultSchema;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Form Schema Dao
 */
@Component
public class FormSchemaDao {

  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoFormBlueprint> AO_FORM_DATA_BLUEPRINT_TYPE = AoFormBlueprint.class;
  private static final Class<AoFormSchema> AO_FORM_DATA_SCHEMA = AoFormSchema.class;

  @Autowired
  public FormSchemaDao(ActiveObjects ao) {
    this.ao = ao;
  }

  /**
   * Get the id of plugin blueprint
   *
   * @return blueprintId or an empty string if none found
   */
  @Nonnull
  public String getBlueprintId() {
    AoFormBlueprint[] blueprints = this.ao.find(AO_FORM_DATA_BLUEPRINT_TYPE, Query.select());
    return blueprints.length == 0 ? "" : blueprints[0].getBlueprintId();
  }

  /**
   * Create new entry to represent the a FormData
   *
   * @param formSchema FormData model object
   * @return FormSchema object created in data store
   */
  @Nullable
  public FormSchema createSchema(@Nonnull FormSchema formSchema) {
    AoFormSchema aoFormSchema = this.ao.create(AO_FORM_DATA_SCHEMA);

    this.prepareAOSchema(aoFormSchema, formSchema);

    // Save the changes to the active object
    aoFormSchema.save();

    return this.asSchema(aoFormSchema);
  }

  /**
   * Retrieve one schema from the database by the global id
   *
   * @param id global id of the schema
   * @return FormSchema object
   */
  @Nullable
  public FormSchema findOneSchema(long id) {
    AoFormSchema aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().limit(1).where("GLOBAL_ID = ?", id))[0];

    return this.asSchema(aoFormSchema);
  }

  /**
   * Retrieve current schema from the database by looking for latest global id
   *
   * @return FormSchema object
   */
  @Nullable
  public FormSchema findCurrentSchema() {
    AoFormSchema[] aoFormSchemas = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().limit(1).order("GLOBAL_ID DESC"));

    // create and return default schema if none is in the database
    if (aoFormSchema.length == 0){
      return this.createSchema(new FormSchema.Builder().withSchema(DefaultSchema.SCHEMA).withUiSchema(
        DefaultSchema.UI_SCHEMA).withIndexSchema(DefaultSchema.INDEX_SCHEMA).build());
    }
    
    return this.asSchema(aoFormSchemas[0]);
  }

  /**
   * Find every schema in the database
   *
   * @return List<FormSchema> every schema in the database.
   */
  public List<FormSchema> findAllSchema() {
    AoFormSchema[] aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select());

    return this.asListFormSchema(aoFormSchema);
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @return List<FormSchema>
   */
  private List<FormSchema> asListFormSchema(@Nonnull AoFormSchema[] aoFormSchemas) {
    return Arrays.stream(aoFormSchemas)
      .map(this::asSchema)
      .collect(Collectors.toList());
  }

  /**
   * Prepare form data active object with the data from a form data
   *
   * @param aoFormSchema    active object
   * @param formSchema with data to be added to the active object
   */
  private void prepareAOSchema(@Nonnull AoFormSchema aoFormSchema, @Nonnull FormSchema formSchema) {
    aoFormSchema.setSchema(formSchema.getSchema());
    aoFormSchema.setUiSchema(formSchema.getUiSchema());
    aoFormSchema.setIndexSchema(formSchema.getIndexSchema());
    aoFormSchema.setDescription(formSchema.getDescription());
    aoFormSchema.setName(formSchema.getName());
    aoFormSchema.setVersion(formSchema.getVersion());
  }

  /**
   * Convert form data active object to a form data model object
   *
   * @param aoFormSchema active object to be converted
   * @return FormSchema object
   */
  @Nullable
  private FormSchema asSchema(@Nullable AoFormSchema aoFormSchema) {
    return aoFormSchema == null
      ? null
      : (new FormSchema.Builder())
      .withGlobalId(aoFormSchema.getGlobalId())
      .withSchema(aoFormSchema.getSchema())
      .withUiSchema(aoFormSchema.getUiSchema())
      .withIndexSchema(aoFormSchema.getIndexSchema())
      .withName(aoFormSchema.getName())
      .withDescription(aoFormSchema.getDescription())
      .withVersion(aoFormSchema.getVersion())
      .build();
  }
}
