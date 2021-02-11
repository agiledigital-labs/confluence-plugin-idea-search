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
  FedexSchema createSchema(FedexSchema fedexSchema);

  List<FedexSchema> listSchemas();

  /**
   * Update an idea in the data store by the contentID
   *
   * @param fedexIdea updated
   * @param contentId of idea to be updated
   * @return updated fedex idea
   */
  FedexIdea updateIdea(FedexIdea fedexIdea, long contentId);

  FedexSchema getSchema(long id);

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

  List<FedexIdea> queryAllFedexIdea(String title, String description, String status, String owner);
}
