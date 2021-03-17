package au.com.agiledigital.structured_form.dao;

import au.com.agiledigital.structured_form.helpers.Utilities;
import au.com.agiledigital.structured_form.model.FormSchema;
import au.com.agiledigital.structured_form.helpers.DefaultSchema;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static au.com.agiledigital.structured_form.helpers.Utilities.asSchema;

/**
 * Form Schema Dao
 *
 * Used to interact with the Active Objects version of the form schema
 *
 * The latest schema is the one with the highest global id as it
 * auto increments.
 * Old versions of the schema are maintained in the database,
 */
@Component
public class FormSchemaDao {

  public static final String GLOBAL_ID = "GLOBAL_ID = ?";
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

    return asSchema(aoFormSchema);
  }

  public FormSchema upsertSchema(FormSchema formSchema){
    AoFormSchema aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().where(GLOBAL_ID, formSchema.getGlobalId()))[0];
    this.prepareAOSchema(aoFormSchema, formSchema);

    aoFormSchema.save();

    return asSchema(aoFormSchema);
  }

  @Nonnull
  private Pair<AoFormSchema, AoFormSchema> versionUpFormSchema(FormSchema formSchema) {
    AoFormSchema existingAoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().where(GLOBAL_ID, formSchema.getGlobalId()))[0];
    AoFormSchema newAoFormSchema = this.ao.create(AO_FORM_DATA_SCHEMA);
    existingAoFormSchema.setIsDefault(false);
    newAoFormSchema.setIsDefault(existingAoFormSchema.getIsDefault());
    this.prepareAOSchema(newAoFormSchema, formSchema);
    newAoFormSchema.setVersion(existingAoFormSchema.getVersion() + 1);

    return  Pair.of(existingAoFormSchema, newAoFormSchema);
  }
  public FormSchema newVersion(FormSchema formSchema){
    Pair<AoFormSchema, AoFormSchema> formSchemas = versionUpFormSchema(formSchema);
    formSchemas.getRight().save();
    formSchemas.getLeft().save();
    return asSchema(formSchemas.getLeft());
  }


  public FormSchema newVersion(FormSchema formSchema, boolean updateFormData){
    Pair<AoFormSchema, AoFormSchema> formSchemas = versionUpFormSchema(formSchema);

    if(updateFormData){
      Arrays.stream(formSchemas.getRight().getFormData()).forEach(aoFormData -> {
        AoFormSchema newS = formSchemas.getLeft();
        aoFormData.setFormSchema(newS);
        aoFormData.save();
      });
    }

    formSchemas.getRight().save();
    formSchemas.getLeft().save();
    return asSchema(formSchemas.getLeft());
  }

  /**
   * Retrieve one schema from the database by the global id
   *
   * @param id global id of the schema
   * @return FormSchema object
   */
  @Nullable
  public FormSchema findOneSchema(long id) {
    AoFormSchema aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().limit(1).where(GLOBAL_ID, id))[0];

    return asSchema(aoFormSchema);
  }

  /**
   * Retrieve current schema from the database by looking for latest global id
   *
   * @return FormSchema object
   */
  @Nullable
  public FormSchema findDefaultSchema() {
    AoFormSchema[] aoFormSchemas = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().where("IS_DEFAULT = ?", true).order("GLOBAL_ID DESC"));

    // create and return default schema if none is in the database
    if (aoFormSchemas == null || aoFormSchemas.length == 0){
      AoFormSchema aoFormSchema = this.ao.create(AO_FORM_DATA_SCHEMA);
      aoFormSchema.setSchema(DefaultSchema.SCHEMA);
      aoFormSchema.setUiSchema(DefaultSchema.UI_SCHEMA);
      aoFormSchema.setIndexSchema(DefaultSchema.INDEX_SCHEMA);
      aoFormSchema.setIsDefault(true);
      aoFormSchema.setName("Auto generated default schema");
      aoFormSchema.save();
      return asSchema(aoFormSchema);
    }

    return asSchema(aoFormSchemas[0]);
  }

  /**
   * Find every schema in the database
   *
   * @return List<FormSchema> every schema in the database.
   */
  public List<FormSchema> findAllSchema() {
    AoFormSchema[] aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA);

    return this.asListFormSchema(aoFormSchema);
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @return List<FormSchema>
   */
  private List<FormSchema> asListFormSchema(@Nonnull AoFormSchema[] aoFormSchemas) {
    return Arrays.stream(aoFormSchemas)
      .map(Utilities::asSchema)
      .collect(Collectors.toList());
  }

  private AoFormSchema[] getDefaultSchema(){
    return this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().where("IS_DEFAULT = ?", true));
  }

  private void removeExistingDefaultSchemas(){
  AoFormSchema[] schemasSetAsDefault = this.getDefaultSchema();
  // remove old defaults
    Arrays.stream(schemasSetAsDefault).forEach(schema -> {
      schema.setIsDefault(false);
      schema.save();
    });

  }

  /**
   * Sets the AoForSchema as the new default schema
   *
   * @param aoFormSchema object from the doa
   */
  public void setSchemaAsDefault(AoFormSchema aoFormSchema){
    removeExistingDefaultSchemas();
    aoFormSchema.setIsDefault(true);
    aoFormSchema.save();
  }

  /**
   * Sets the FormSchema as the new default schema
   *
   * @param formSchema model object from the plugin
   */
  public void setSchemaAsDefault(FormSchema formSchema){
    removeExistingDefaultSchemas();
    AoFormSchema aoFormSchema = this.ao.find(AO_FORM_DATA_SCHEMA, Query.select().where(GLOBAL_ID, formSchema.getGlobalId()))[0];
    aoFormSchema.setIsDefault(true);
    aoFormSchema.save();
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
    aoFormSchema.setIsDefault(formSchema.getIsDefault());
  }


}
