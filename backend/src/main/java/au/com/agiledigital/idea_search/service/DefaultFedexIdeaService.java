package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import au.com.agiledigital.idea_search.rest.TechnologyAPI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
  public FedexIdea createIdea(FedexIdea fedexIdea) {

    fedexIdea.getContentId();

    return this.fedexIdeaDao.createIdea(fedexIdea);
  }

  /**
   * Create a new FedexIdea
   *
   * @param fedexSchema to be created
   * @return FedexIdea that was created
   */
  public FedexSchema createSchema(FedexSchema fedexSchema) {
    return this.fedexIdeaDao.createSchema(fedexSchema);
  }

  public  FedexSchema getSchema(long id) {
    return this.fedexIdeaDao.findOneSchema(id);
  }

  public List<FedexSchema> listSchemas() {
    return this.fedexIdeaDao.findAllSchema();
  }

  public FedexIdea getByContentId(long contentId){
    return this.fedexIdeaDao.getByContentId(contentId);
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
  public FedexIdea updateIdea(FedexIdea fedexIdea, long contentId) {
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

  public List<FedexIdea> queryAllFedexIdea() { return fedexIdeaDao.findAll(); }
}
