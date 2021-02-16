package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;

import java.util.List;

public interface FedexIdeaService {

  /**
   * Create an idea in the data store
   *
   * @param fedexIdea to be created in the store
   * @return created fedexIdea
   */
  FedexIdea createIdea(FedexIdea fedexIdea);

  /**
   * Saves the supplied FedexSchema object
   *
   * @param fedexSchema to be saved in the active object database
   * @return
   */
  FedexSchema createSchema(FedexSchema fedexSchema);

  /**
   * Lists all FedexSchema
   *
   * @return a list of FedexSchema
   */
  List<FedexSchema> listSchemas();

  /**
   * Update an idea in the data store by the contentID
   *
   * @param fedexIdea updated
   * @param contentId of idea to be updated
   * @return updated fedex idea
   */
  FedexIdea upsertIdea(FedexIdea fedexIdea, long contentId);

  /**
   * Gets a schema by id
   *
   * @param id of the FedexSchema
   * @return FedexSchema with matching id
   */
  FedexSchema getSchema(long id);

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  FedexSchema getCurrentSchema();

  /**
   * Gets a FedexIdea by contentId
   *
   * @param contentId of the FedexIdea
   * @return FedexIdea with matching contentId
   */
  FedexIdea getByContentId(long contentId);

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

  List<FedexIdea> queryAllFedexIdea();
}
