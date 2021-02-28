package au.com.agiledigital.structured_form.service;

import au.com.agiledigital.structured_form.dao.AoFormData;
import au.com.agiledigital.structured_form.dao.FormDataDao;
import au.com.agiledigital.structured_form.dao.FormSchemaDao;
import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormIndex;
import au.com.agiledigital.structured_form.model.FormIndexQuery;
import au.com.agiledigital.structured_form.model.FormSchema;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static au.com.agiledigital.structured_form.helpers.Utilities.asFormData;
import static au.com.agiledigital.structured_form.helpers.Utilities.getUsername;

public class DefaultFormDataService implements FormDataService {
  private final FormDataDao formDataDao;
  private final FormSchemaDao formSchemaDao;
  private final Gson gson = new Gson();
  @ComponentImport
  private final PageService pageService;
  @ComponentImport
  private final UserAccessor userAccessor;


  @Autowired
  public DefaultFormDataService(FormDataDao formDataDao, FormSchemaDao formSchemaDao, PageService pageService, UserAccessor userAccessor) {
    this.formDataDao = formDataDao;
    this.formSchemaDao = formSchemaDao;
    this.pageService = pageService;
    this.userAccessor = userAccessor;
  }

  /**
   * Create a new FormData
   *
   * @param formSchema to be created
   * @return FormData that was created
   */
  @Nullable
  public FormSchema createSchema(@Nonnull FormSchema formSchema) {
    FormSchema test = this.formSchemaDao.createSchema(formSchema);
    AoFormData[] formDataDao =this.formDataDao.findAll();
    if (formDataDao.length>0) {
  this.updateIndex(formSchema);
}

    return test;
  }

  private void updateIndex(@Nonnull FormSchema formSchema) throws NullPointerException {
        AoFormData[] data = this.formDataDao.findAll();
        Arrays.asList(data).forEach(ao ->
          this.formDataDao.updateIndexValues(ao, this.getIndexData(ao, formSchema.getIndexSchema()))
        );

  }

  /**
   * Gets the schema with query id
   *
   * @param id of the requested schema
   * @return FormSchema for the id
   */
  @Nullable
  public FormSchema getSchema(long id) {
    return this.formSchemaDao.findOneSchema(id);
  }

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  public List<FormSchema> listSchemas() {
    return this.formSchemaDao.findAllSchema();
  }

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  @Nullable
  public FormSchema getCurrentSchema() {
    return this.formSchemaDao.findCurrentSchema();
  }

  /**
   * Gets a Form Data by content id
   *
   * @param contentId of the FormData
   * @return FormData by the confluence content id
   */
  public FormData getByContentId(long contentId) {
    return this.formDataDao.getByContentId(contentId);
  }

  /**
   * Gets the existing blueprint id from database
   *
   * @return the current blueprint id
   */
  @Nonnull
  public String getBlueprintId() {
    return this.formDataDao.getBlueprintId();
  }

  /**
   * Store a blueprint id in the database
   *
   * @param blueprintId to be set
   */
  public void setBlueprintId(String blueprintId) {
    this.formDataDao.setBlueprintId(blueprintId);
  }

  /**
   * Create or Update an existing FormData
   *
   * @param formData  to be updated
   * @param contentId of formData to be updated
   */
  public void upsertFormData(@Nonnull FormData formData, long contentId) {
    Set<FormIndex> indexes = getIndexData(formData);


    this.formDataDao.upsertByContentId(formData, contentId, indexes);
  }

  /**
   * Return every formData from the database
   *
   * @return List<FormData> with no filtering or selection
   */
  public List<FormData> queryAllFormData() {

    return asListFormData(formDataDao.find());
  }

  public List<FormData> queryAllFormData(@Nonnull List<FormIndexQuery> search) {

    return asListFormData(formDataDao.find(search, 0, 10));
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFormData list of active object formData to be converted to a list of the model
   *                   FormData
   * @return List<FormData>
   */
  private List<FormData> asListFormData(@Nonnull AoFormData[] aoFormData) {

    return Arrays.stream(aoFormData).filter(form -> {
      form.getFormData();
      return true;
    })
      .map(formData -> asFormData(formData, this.pageService, getUsername(formData.getCreatorUserKey(), this.userAccessor), getIndexData(formData)))
      .collect(Collectors.toList());
  }





  @Nonnull
  private Set<FormIndex> getIndexData(@Nonnull AoFormData aoFormData, String indexSchema) {

    return getFormIndices(asFormData(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor)), indexSchema);
  }

  @Nonnull
  private Set<FormIndex> getIndexData(@Nonnull AoFormData aoFormData) {

    return  getFormIndices(asFormData(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor)));
  }

  @Nonnull
  private Set<FormIndex> getIndexData(@Nonnull FormData formData) {

    return getFormIndices(formData);
  }

  @Nonnull
  private Set<FormIndex> getFormIndices(@Nonnull FormData formData) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(this.formSchemaDao.findCurrentSchema().getIndexSchema(), LinkedHashMap.class);
    return getFormIndices(formData, jsonIndexSchema);
  }
  @Nonnull
  private Set<FormIndex> getFormIndices(@Nonnull FormData formData, String indexSchema) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(indexSchema, LinkedHashMap.class);
    return getFormIndices(formData, jsonIndexSchema);
  }


  @Nonnull
  private Set<FormIndex> getFormIndices(@Nonnull FormData formData, LinkedHashMap<String, String> jsonIndexSchema) {


    JsonElement jsonElementStringIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("index");
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
      jsonElementStringIndexSchema.getAsJsonArray().iterator(), Spliterator.ORDERED), false).filter(Objects::nonNull)
      .map(element -> this.createIndex(element, formData)).collect(Collectors.toSet());
  }

  @Nonnull
  private FormIndex createIndex(@Nonnull JsonElement indexElement, @Nonnull FormData formData) {
     String key = indexElement.getAsJsonObject().get("key").getAsString();
     String type = indexElement.getAsJsonObject().get("type").getAsString().toUpperCase();
     JsonElement index = indexElement.getAsJsonObject().get("index");
    LinkedHashMap<String, ?> jsonFromData = gson.fromJson(formData.getFormDataValue(), LinkedHashMap.class);
     if (index != null && jsonFromData != null) {
       return new FormIndex(jsonFromData.get(key), index, type, key);
     }else if(jsonFromData.get(key) == null ){
       return new FormIndex(formData.get(key), type, key);
     }
     else{
       return new FormIndex(jsonFromData.get(key).toString(), type, key);

     }
  }

}
