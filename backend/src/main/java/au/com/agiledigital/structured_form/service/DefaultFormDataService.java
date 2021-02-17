package au.com.agiledigital.structured_form.service;

import au.com.agiledigital.structured_form.dao.AoFromData;
import au.com.agiledigital.structured_form.dao.FormDataDao;
import au.com.agiledigital.structured_form.dao.FormSchemaDao;
import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormSchema;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static au.com.agiledigital.structured_form.helpers.Utilities.asFedexIdea;
import static au.com.agiledigital.structured_form.helpers.Utilities.getUsername;

public class DefaultFormDataService implements FormDataService {
  private final FormDataDao formDataDao;
  private final FormSchemaDao formSchemaDao;
  private Gson gson = new Gson();
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
   * @param formData to be created
   * @return FormData that was created
   */
  public FormData createIdea(FormData formData) {

    return this.formDataDao.createIdea(formData);
  }

  /**
   * Create a new FormData
   *
   * @param formSchema to be created
   * @return FormData that was created
   */
  public FormSchema createSchema(FormSchema formSchema) {
    return this.formSchemaDao.createSchema(formSchema);
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
   * @param formData to be updated
   * @param contentId of idea to be updated
   * @return FormData that was updated
   */
  public FormData upsertIdea(FormData formData, long contentId) {
    return this.formDataDao.upsertByContentId(formData, contentId);
  }

  /**
   * Return every idea from the database
   *
   * @return List<FormData> with no filtering or selection
   */
  public List<FormData> queryAllFedexIdea() {

    return asListFedexIdea(formDataDao.findAll());

  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFromData list of active object ideas to be converted to a list of the model
   *                     FormData
   * @return List<FormData>
   */
  private List<FormData> asListFedexIdea(AoFromData[] aoFromData) {

    return Arrays.stream(aoFromData)
      .map(aoIdea -> asFedexIdea(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor), getIndexData(aoIdea)))
      .collect(Collectors.toList());
  }


  private List<String> getIndexData(AoFromData idea) {
    LinkedHashMap<String, String> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);
//{"index":["firstName","telephone"]}
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(this.formSchemaDao.findCurrentSchema().getIndexSchema(), LinkedHashMap.class);
    JsonElement jsonElementIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("index");

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
      jsonElementIndexSchema.getAsJsonArray().iterator(), Spliterator.ORDERED), false)
      .map(r -> jsonFromData.get(r.getAsString()))
      .collect(Collectors.toList());

  }
}


