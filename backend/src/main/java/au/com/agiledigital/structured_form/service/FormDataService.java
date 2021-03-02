package au.com.agiledigital.structured_form.service;

import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormIndexQuery;
import au.com.agiledigital.structured_form.model.FormSchema;

import java.util.List;

public interface FormDataService {

  /**
   * Saves the supplied FormSchema object
   *
   * @param formSchema to be saved in the active object database
   * @return form schema saved to the database
   */
  FormSchema createSchema(FormSchema formSchema);

  /**
   * Lists all FormSchema
   *
   * @return a list of FormSchema
   */
  List<FormSchema> listSchemas();

  /**
   * Update an formData in the data store by the contentID
   *
   * @param formData updated
   * @param contentId of formData to be updated
   */
  void upsertFormData(FormData formData, long contentId);

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

  /**
   * Return every formData from the database
   *
   * @return List<FormData> with no filtering or selection
   */
  List<FormData> queryAllFormData();
  /**
   * Search for formData that match a list of queries.
   * Results limited to ten.
   *
   * @param search at FormIndexQuery to be passed is as a where clause to the db
   * @return List of FormData rows that match the queries
   */
  List<FormData> queryAllFormData(List<FormIndexQuery> search);
}
