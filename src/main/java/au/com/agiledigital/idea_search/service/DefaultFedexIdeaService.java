package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultFedexIdeaService implements FedexIdeaService {

  private final FedexIdeaDao fedexIdeaDao;

  @Autowired
  public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao) {
    this.fedexIdeaDao = fedexIdeaDao;
  }

  /**
   * Create a new FedexIdea
   *
   * @param fedexIdea to be created
   * @return FedexIdea that was created
   */
  public FedexIdea create(FedexIdea fedexIdea) {
    return this.fedexIdeaDao.create(fedexIdea);
  }

  /**
   * Get the existing blueprint id from database
   *
   * @return the current blueprint id
   */
  public String getBlueprintId() {
    return this.fedexIdeaDao.getBlueprintId();
  }

  /**
   * Store a blueprint id in the database
   *
   * @param blueprintId the blueprint id to be set
   */
  public void setBlueprintId(String blueprintId) {
    this.fedexIdeaDao.setBlueprintId(blueprintId);
  }

  /**
   * Update an existing FedexIdea
   *
   * @param fedexIdea to be updated
   * @param contentId of idea to be updated
   * @return FedexIdea that was updated
   */
  public FedexIdea update(FedexIdea fedexIdea, long contentId) {
    return this.fedexIdeaDao.upsertByContentId(fedexIdea, contentId);
  }

  /**
   * Pass through a list of distinct technology strings from dao Overload to take a search string
   *
   * @param searchString of the technology be searched for
   * @return A string list of technology names
   */
  public List<TechnologyAPI> queryTechList(String searchString) {
    return fedexIdeaDao.queryTechList(searchString);
  }

  /**
   * Pass through a list of distinct technology strings from dao
   *
   * @return A string list of technology names
   */
  public List<TechnologyAPI> queryTechList() {
    return fedexIdeaDao.queryTechList();
  }
}
