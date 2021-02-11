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

  /**
   * Gets the schema with query id
   *
   * @param id of the requested schema
   * @return
   */
  public  FedexSchema getSchema(long id) {
    return this.fedexIdeaDao.findOneSchema(id);
  }

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  public List<FedexSchema> listSchemas() {
    return this.fedexIdeaDao.findAllSchema();
  }

  /**
   * Gets a fedex idea by content id
   *
   * @param contentId of the FedexIdea
   * @return
   */
  public FedexIdea getByContentId(long contentId){
    return this.fedexIdeaDao.getByContentId(contentId);
  }

  /**
   * Gets the existing blueprint id from database
   *
   * @return the current blueprint id
   */
  public String getBlueprintId() {
    return this.fedexIdeaDao.getBlueprintId();
  }

  /**
   * Store a blueprint id in the database
   *
   * @param blueprintId to be set
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

  public List<FedexIdea> queryAllFedexIdea(String title, String description, String status, String owner) { return fedexIdeaDao.findAll(title, description, status, owner); }
}
