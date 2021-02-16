package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.dao.FedexSchemaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultFedexIdeaService implements FedexIdeaService {
  private final FedexIdeaDao fedexIdeaDao;
  private final FedexSchemaDao fedexSchemaDao;

  @Autowired
  public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao, FedexSchemaDao fedexSchemaDao) {
    this.fedexIdeaDao = fedexIdeaDao;
    this.fedexSchemaDao = fedexSchemaDao;
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
    return this.fedexSchemaDao.createSchema(fedexSchema);
  }

  /**
   * Gets the schema with query id
   *
   * @param id of the requested schema
   * @return FedexSchema for the id
   */
  public FedexSchema getSchema(long id) {
    return this.fedexSchemaDao.findOneSchema(id);
  }

  /**
   * Lists all schema
   *
   * @return a list of schemas
   */
  public List<FedexSchema> listSchemas() {
    return this.fedexSchemaDao.findAllSchema();
  }

  /**
   * Gets a fedex idea by content id
   *
   * @param contentId of the FedexIdea
   * @return FedexId by the confluence content id
   */
  public FedexIdea getByContentId(long contentId) {
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
   * Create or Update an existing FedexIdea
   *
   * @param fedexIdea to be updated
   * @param contentId of idea to be updated
   * @return FedexIdea that was updated
   */
  public FedexIdea upsertIdea(FedexIdea fedexIdea, long contentId) {
    return this.fedexIdeaDao.upsertByContentId(fedexIdea, contentId);
  }

  /**
   * Return every idea from the database
   *
   * @return List<FedexIdea> with no filtering or selection
   */
  public List<FedexIdea> queryAllFedexIdea() {
    return fedexIdeaDao.findAll();
  }
}
