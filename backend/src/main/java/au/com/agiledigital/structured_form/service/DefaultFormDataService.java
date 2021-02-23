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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static au.com.agiledigital.structured_form.helpers.Utilities.*;

public class DefaultFormDataService implements FormDataService {
  private final FormDataDao formDataDao;
  private final FormSchemaDao formSchemaDao;
  private Gson gson = new Gson();
  @ComponentImport
  private final PageService pageService;
  @ComponentImport
  private final UserAccessor userAccessor;

  private Logger log = LoggerFactory.getLogger(DefaultFormDataService.class);

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
   * @param formData to be created
   * @return FormData that was created
   */
  public FormData createIdea(FormData formData) {
    Set<FormIndex> indexes = getIndexData(formData);
    return this.formDataDao.createIdea(formData, indexes);
  }

  /**
   * Create a new FormData
   *
   * @param formSchema to be created
   * @return FormData that was created
   */
  public FormSchema createSchema(FormSchema formSchema) {
    FormSchema test = this.formSchemaDao.createSchema(formSchema);

    this.updateIndex( formSchema);

    return test;
  }

  private boolean updateIndex(FormSchema formSchema) {
    try {

        AoFormData[] data = this.formDataDao.findAll();
        Arrays.asList(data).forEach(ao ->
          this.formDataDao.updateIndexValues(ao, this.getIndexData(ao, formSchema.getIndexSchema()))
        );

      return true;
    } catch (Throwable t){
      return false;
    }
  }


  /**
   * Gets the schema with query id
   *
   * @param id of the requested schema
   * @return FormSchema for the id
   */
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
  public FormSchema getCurrentSchema() {
    return this.formSchemaDao.findCurrentSchema();
  }

  /**
   * Gets a fedex idea by content id
   *
   * @param contentId of the FormData
   * @return FedexId by the confluence content id
   */
  public FormData getByContentId(long contentId) {
    return this.formDataDao.getByContentId(contentId);
  }

  /**
   * Gets the existing blueprint id from database
   *
   * @return the current blueprint id
   */
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
   * @param contentId of idea to be updated
   * @return FormData that was updated
   */
  public FormData upsertIdea(FormData formData, long contentId) {
    Set<FormIndex> indexes = getIndexData(formData);


    return this.formDataDao.upsertByContentId(formData, contentId, indexes);
  }

  /**
   * Return every idea from the database
   *
   * @return List<FormData> with no filtering or selection
   */
  public List<FormData> queryAllFedexIdea() {

    return asListFedexIdea(formDataDao.find());
  }

  public List<FormData> queryAllFedexIdea(List<FormIndexQuery> search) {

    return asListFedexIdea(formDataDao.find(search, 0, 10));
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFormData list of active object ideas to be converted to a list of the model
   *                   FormData
   * @return List<FormData>
   */
  private List<FormData> asListFedexIdea(AoFormData[] aoFormData) {

    return Arrays.stream(aoFormData).filter(form -> form.getFormData() != null)
      .map(aoIdea -> asFedexIdea(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor), getIndexData(aoIdea)))
      .collect(Collectors.toList());
  }





  private Set<FormIndex> getIndexData(AoFormData idea, String indexSchema) {

    return getFormIndices(asFedexIdea(idea, this.pageService, getUsername(idea.getCreatorUserKey(), this.userAccessor)), indexSchema);
  }

  private Set<FormIndex> getIndexData(AoFormData idea) {

    return  getFormIndices(asFedexIdea(idea, this.pageService, getUsername(idea.getCreatorUserKey(), this.userAccessor)));
  }

  private Set<FormIndex> getIndexData(FormData idea) {

    return getFormIndices(idea);
  }

  @Nonnull
  private Set<FormIndex> getFormIndices(FormData idea) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(this.formSchemaDao.findCurrentSchema().getIndexSchema(), LinkedHashMap.class);
    return getFormIndices(idea, jsonIndexSchema);
  }
  @Nonnull
  private Set<FormIndex> getFormIndices(FormData idea, String indexSchema) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(indexSchema, LinkedHashMap.class);
    return getFormIndices(idea, jsonIndexSchema);
  }


  @Nonnull
  private Set<FormIndex> getFormIndices(FormData idea, LinkedHashMap<String, String> jsonIndexSchema) {


//    {"stringIndex":["firstName","lastName"],
//      "numberIndex":[  "age", "telephone"]
//    }

//    "index": [{
//    "key": "firstName",
//    "index": 1,
//    "type": "string"
//    }, {
//    "key": "lastName",
//    "index": 2
//    "type": "string"
//    },{
//    "key": "title"
//    "type": "static"
//    }]

    JsonElement jsonElementStringIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("index");
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
      jsonElementStringIndexSchema.getAsJsonArray().iterator(), Spliterator.ORDERED), false)
      .map(element -> this.createIndex(element, idea)).collect(Collectors.toSet());

  }

  private FormIndex createIndex(JsonElement indexElement, FormData idea) {
     String key = indexElement.getAsJsonObject().get("key").getAsString();
     String type = indexElement.getAsJsonObject().get("type").getAsString().toUpperCase();
     JsonElement index = indexElement.getAsJsonObject().get("index");
    LinkedHashMap<String, ?> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);
     if (index != null) {
       return new FormIndex(jsonFromData.get(key), index, type, key);
     }else if(jsonFromData.get(key) == null){
       return new FormIndex(idea.get(key), type, key);
     }
     else{
       return new FormIndex(jsonFromData.get(key).toString(), type, key);

     }
  }

}

/**
 * [{
 *   "creator": "admin",
 *   "indexData": [{"index": 2, "type": "string", "value": "asdf", "key": "lastName"}, {
 *     "index": -2147483648,
 *     "type": "static",
 *     "value": "asdf",
 *     "key": "title"
 *   }, {"index": 1, "type": "string", "value": "asd", "key": "firstName"}],
 *   "title": "asdf",
 *   "url": "http://wren:1990/confluence/display/ds/asdf"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": -2147483648, "type": "static", "value": "fdsg", "key": "title"}, {
 *     "index": 2,
 *     "type": "string",
 *     "value": "df",
 *     "key": "lastName"
 *   }, {"index": 1, "type": "string", "value": "dv", "key": "firstName"}],
 *   "title": "fdsg",
 *   "url": "http://wren:1990/confluence/display/ds/fdsg"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": 1, "type": "string", "value": "asdf", "key": "firstName"}, {
 *     "index": 2,
 *     "type": "string",
 *     "value": "asfd",
 *     "key": "lastName"
 *   }, {"index": -2147483648, "type": "static", "value": "asf", "key": "title"}],
 *   "title": "asf",
 *   "url": "http://wren:1990/confluence/display/ds/asf"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": 1, "type": "string", "value": "sdfg", "key": "firstName"}, {
 *     "index": 2,
 *     "type": "string",
 *     "value": "sdfg",
 *     "key": "lastName"
 *   }, {"index": -2147483648, "type": "static", "value": "sdfg", "key": "title"}],
 *   "title": "sdfg",
 *   "url": "http://wren:1990/confluence/display/ds/sdfg"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": 2, "type": "string", "value": "Mca", "key": "lastName"}, {
 *     "index": -2147483648,
 *     "type": "static",
 *     "value": "Title",
 *     "key": "title"
 *   }, {"index": 1, "type": "string", "value": "Rboi", "key": "firstName"}],
 *   "title": "Title",
 *   "url": "http://wren:1990/confluence/display/ds/Title"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": -2147483648, "type": "static", "value": "fdg", "key": "title"}, {
 *     "index": 1,
 *     "type": "string",
 *     "key": "firstName"
 *   }, {"index": 2, "type": "string", "key": "lastName"}],
 *   "title": "fdg",
 *   "url": "http://wren:1990/confluence/display/ds/fdg"
 * }, {
 *   "creator": "admin",
 *   "indexData": [{"index": 2, "type": "string", "value": "S", "key": "lastName"}, {
 *     "index": 1,
 *     "type": "string",
 *     "value": "Shove",
 *     "key": "firstName"
 *   }, {"index": -2147483648, "type": "static", "value": "shouv", "key": "title"}],
 *   "title": "shouv",
 *   "url": "http://wren:1990/confluence/display/ds/shouv"
 * }]
 */
