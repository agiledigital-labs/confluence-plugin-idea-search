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

  private void updateIndex(FormSchema formSchema) {
    try {
      int size = this.formDataDao.size();

      for (int i = 0; i < (size / 4); i = i + 4) {

        AoFormData[] data = this.formDataDao.find(i, i + 4);
        Arrays.asList(data).forEach(ao ->
          this.formDataDao.updateIndexValues(ao, this.getIndexData(ao, formSchema.getIndexSchema()))
        );
      }
    } catch (Throwable t){
      log(t.toString());
    }
  }

  private void log(String toString) {
    log(toString);
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

    return asListFedexIdea(formDataDao.findAll());
  }

  public List<FormData> queryAllFedexIdea(List<FormIndexQuery> search) {

    return asListFedexIdea(formDataDao.findAll(search));
  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFormData list of active object ideas to be converted to a list of the model
   *                   FormData
   * @return List<FormData>
   */
  private List<FormData> asListFedexIdea(AoFormData[] aoFormData) {

    return Arrays.stream(aoFormData)
      .map(aoIdea -> asFedexIdea(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor), getIndexData(aoIdea)))
      .collect(Collectors.toList());
  }


  private void createIndex(JsonElement indexElement, int index, LinkedHashMap<String, ?> jsonFromData, Set<FormIndex> indexSet, boolean shouldBeNumber) {
    String key = indexElement.getAsString();
    if (jsonFromData != null && jsonFromData.containsKey(key) && (shouldBeNumber)) {
      indexSet.add(new FormIndex(jsonFromData.get(key).toString(), index, true));
    } else if (jsonFromData != null && jsonFromData.containsKey(key) && (!shouldBeNumber)) {
      indexSet.add(new FormIndex(jsonFromData.get(key).toString(), index, false));
    }
  }


  private Set<FormIndex> getIndexData(AoFormData idea, String indexSchema) {
    LinkedHashMap<String, ?> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);

    return (Set<FormIndex>) getFormIndices(jsonFromData, indexSchema);
  }

  private Set<FormIndex> getIndexData(AoFormData idea) {
    LinkedHashMap<String, ?> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);

    return (Set<FormIndex>) getFormIndices(jsonFromData);
  }

  private Set<FormIndex> getIndexData(FormData idea) {
    LinkedHashMap<String, ?> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);

    return (Set<FormIndex>) getFormIndices(jsonFromData);
  }

  @Nonnull
  private Set<FormIndex> getFormIndices(LinkedHashMap<String, ?> jsonFromData) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(this.formSchemaDao.findCurrentSchema().getIndexSchema(), LinkedHashMap.class);
    return getFormIndices(jsonFromData, jsonIndexSchema);
  }
  @Nonnull
  private Set<FormIndex> getFormIndices(LinkedHashMap<String, ?> jsonFromData, String indexSchema) {
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(indexSchema, LinkedHashMap.class);
    return getFormIndices(jsonFromData, jsonIndexSchema);
  }

  @Nonnull
  private Set<FormIndex> getFormIndices(LinkedHashMap<String, ?> jsonFromData, LinkedHashMap<String, String> jsonIndexSchema) {
    JsonElement jsonElementStringIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("stringIndex");
    JsonElement jsonElementNumberIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("numberIndex");

    Set<FormIndex> index = new HashSet<>(Collections.emptySet());
    jsonElementStringIndexSchema.getAsJsonArray().forEach(withCounter((i, r) -> createIndex(r, i, jsonFromData, index, false)));
    jsonElementNumberIndexSchema.getAsJsonArray().forEach(withCounter((i, r) -> createIndex(r, i, jsonFromData, index, true)));
    return index;
  }
}


