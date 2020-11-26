package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.model.FedexIdea;
import java.util.List;

public interface FedexIdeaService {
  /**
   * Create an idea in the data store
   * @param fedexIdea to be created in the store
   * @return created fedexIdea
   */
  FedexIdea create(FedexIdea fedexIdea);

  /**
   * Update an idea in the data store by the contentID
   * @param fedexIdea updated
   * @param contentId of idea to be updated
   * @return updated fedex idea
   */
  FedexIdea update(FedexIdea fedexIdea, long contentId);
  List<String> techList();
}
