package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.AoFedexIdea;
import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.dao.FedexSchemaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexSchema;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static au.com.agiledigital.idea_search.helpers.Utilities.asFedexIdea;
import static au.com.agiledigital.idea_search.helpers.Utilities.getUsername;

public class DefaultFedexIdeaService implements FedexIdeaService {
  private final FedexIdeaDao fedexIdeaDao;
  private final FedexSchemaDao fedexSchemaDao;
  private Gson gson = new Gson();
  @ComponentImport
  private final PageService pageService;
  @ComponentImport
  private final UserAccessor userAccessor;

  @Autowired
  public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao, FedexSchemaDao fedexSchemaDao, PageService pageService, UserAccessor userAccessor) {
    this.fedexIdeaDao = fedexIdeaDao;
    this.fedexSchemaDao = fedexSchemaDao;
    this.pageService = pageService;
    this.userAccessor = userAccessor;
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
   * Lists all schema
   *
   * @return a list of schemas
   */
  public FedexSchema getCurrentSchema() {
    return this.fedexSchemaDao.findCurrentSchema();
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

    return asListFedexIdea(fedexIdeaDao.findAll());

  }

  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFedexIdeas list of active object ideas to be converted to a list of the model
   *                     FedexIdea
   * @return List<FedexIdea>
   */
  private List<FedexIdea> asListFedexIdea(AoFedexIdea[] aoFedexIdeas) {

    return Arrays.stream(aoFedexIdeas)
      .map(aoIdea -> asFedexIdea(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor), getIndexData(aoIdea)))
      .collect(Collectors.toList());
  }


  private List<String> getIndexData(AoFedexIdea idea) {
    LinkedHashMap<String, String> jsonFromData = gson.fromJson(idea.getFormData(), LinkedHashMap.class);
//{"index":["firstName","telephone"]}
    LinkedHashMap<String, String> jsonIndexSchema = gson.fromJson(this.fedexSchemaDao.findCurrentSchema().getIndexSchema(), LinkedHashMap.class);
    JsonElement jsonElementIndexSchema = gson.toJsonTree(jsonIndexSchema).getAsJsonObject().get("index");

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
      jsonElementIndexSchema.getAsJsonArray().iterator(), Spliterator.ORDERED), false)
      .map(r -> jsonFromData.get(r.getAsString()))
      .collect(Collectors.toList());

  }
}


