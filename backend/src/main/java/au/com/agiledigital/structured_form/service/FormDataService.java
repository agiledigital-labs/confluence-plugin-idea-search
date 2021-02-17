package au.com.agiledigital.structured_form.service;

import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormSchema;

import java.util.List;

public interface FormDataService {

  /**
   * Create an idea in the data store
   *
   * @param formData to be created in the store
   * @return created formData
   */
  FormData createIdea(FormData formData);

  /**
   * Saves the supplied FormSchema object
   *
   * @param formSchema to be saved in the active object database
   * @return
   */
  FormSchema createSchema(FormSchema formSchema);

  /**
   * Lists all FormSchema
   *
   * @return a list of FormSchema
   */
  List<FormSchema> listSchemas();

  /**
   * Update an idea in the data store by the contentID
   *
   * @param formData updated
   * @param contentId of idea to be updated
   * @return updated fedex idea
   */
  FormData upsertIdea(FormData formData, long contentId);

  /**
   * Gets a schema by id
   *
   * @param id of the FormSchema
   * @return FormSchema with matching id
   */
  FormSchema getSchema(long id);

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  FormSchema getCurrentSchema();

  /**
   * Gets a FormData by contentId
   *
   * @param contentId of the FormData
   * @return FormData with matching contentId
   */
  FormData getByContentId(long contentId);

  /**
   * Get the existing blueprint id from database
   *
   * @return the current blueprint id
   */
  String getBlueprintId();

  /**
   * Store a blueprint id in the database
   *
   * @param blueprintId the blueprint id to be set
   */
  void setBlueprintId(String blueprintId);

  List<FormData> queryAllFedexIdea();
}
