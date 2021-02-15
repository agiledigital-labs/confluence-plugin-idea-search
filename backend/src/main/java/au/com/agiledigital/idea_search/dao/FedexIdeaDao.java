package au.com.agiledigital.idea_search.dao;

import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fedex Idea Dao
 */
@Component
public class FedexIdeaDao {

  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoFedexIdea> AO_FEDEX_IDEA_TYPE = AoFedexIdea.class;
  private static final Class<AoIdeaBlueprint> AO_IDEA_BLUEPRINT_TYPE = AoIdeaBlueprint.class;

  @ComponentImport
  private final UserAccessor userAccessor;
  @ComponentImport
  private final PageService pageService;

  @Autowired
  public FedexIdeaDao(ActiveObjects ao, UserAccessor userAccessor, PageService pageService) {
    this.ao = ao;
    this.userAccessor = userAccessor;
    this.pageService = pageService;
  }

  /**
   * Create new entry to represent the a FedexIdea
   *
   * @param fedexIdea FedexIdea model object
   * @return FedexIdea object created in data store
   */
  public FedexIdea createIdea(FedexIdea fedexIdea) {
    AoFedexIdea aoFedexIdea = this.ao.create(AO_FEDEX_IDEA_TYPE);

    this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

    aoFedexIdea.save();

    return this.asFedexIdea(aoFedexIdea);
  }


  /**
   * Get the id of plugin blueprint
   *
   * @return blueprintId or an empty string if none found
   */
  public String getBlueprintId() {
    AoIdeaBlueprint[] blueprints = this.ao.find(AO_IDEA_BLUEPRINT_TYPE, Query.select());
    return blueprints.length == 0 ? "" : blueprints[0].getBlueprintId();
  }

  /**
   * Set the blueprint id if not current
   *
   * @param blueprintId the supplied blueprint id
   */
  public void setBlueprintId(String blueprintId) {
    AoIdeaBlueprint[] blueprint = this.ao
      .find(AO_IDEA_BLUEPRINT_TYPE, Query.select().where("BLUEPRINT_ID = ?", blueprintId));

    // Check if there is already a blueprint
    if (blueprint.length != 0) {
      // If the existing blueprint id does not match with supplied one,
      // then delete it and create a new one with the supplied id,
      // otherwise keep blueprint id unchanged.
      if (!blueprint[0].getBlueprintId().equals(blueprintId)) {
        this.ao.delete(blueprint);
        createBlueprintIdEntry(blueprintId);
      }
      // If no blueprint id is found,
      // then create a new blueprint id with the supplied one.
    } else {
      createBlueprintIdEntry(blueprintId);
    }
  }

  // Creates a blueprintId in the ao database with supplied blueprint id
  private void createBlueprintIdEntry(String blueprintId) {
    AoIdeaBlueprint newBlueprint = this.ao.create(AO_IDEA_BLUEPRINT_TYPE);
    newBlueprint.setBlueprintId(blueprintId);
    newBlueprint.save();
  }

  /**
   * Update a FedexIdea by the page content id
   *
   * @param fedexIdea New FedexIdea for data store
   * @param contentId of the page containing the idea
   * @return FedexIdea saved to the data store
   */
  public FedexIdea upsertByContentId(FedexIdea fedexIdea, long contentId) {
    AoFedexIdea[] aoFedexIdeas =
      this.ao.find(
        AO_FEDEX_IDEA_TYPE,
        Query.select().where("CONTENT_ID = ?", contentId)
      );

    boolean newIdea = aoFedexIdeas.length == 0;

    // If the title of the page already exists the create event fails and a page update event is fired on a successful save.
    // If this happens the aoFedexIdea will not exist in the data store and will need to be created.
    AoFedexIdea aoFedexIdea = newIdea
      ? this.ao.create(AO_FEDEX_IDEA_TYPE)
      : aoFedexIdeas[0];


    this.prepareAOFedexIdea(aoFedexIdea, fedexIdea);

    aoFedexIdea.save();

    return this.asFedexIdea(aoFedexIdea);
  }

  /**
   * Update a FedexIdea by the page content id
   *
   * @param contentId of the page containing the idea
   * @return FedexIdea saved to the data store
   */
  public FedexIdea getByContentId(long contentId) {

    List<AoFedexIdea> test = Arrays.stream(this.ao.find(
      AO_FEDEX_IDEA_TYPE,
      Query.select().where("CONTENT_ID = ?", contentId)
    )).collect(Collectors.toList());

    return test.stream().map(this::asFedexIdea).collect(Collectors.toList()).get(0);

  }



  /**
   * List all fedex idea in the data store
   *
   * @return a list of all available FedexIdea
   */
  public List<FedexIdea> findAll() {
     Query query = Query
       .select();

    AoFedexIdea[] aoFedexIdeas = this.ao.find(AO_FEDEX_IDEA_TYPE, query);
    return this.asListFedexIdea(aoFedexIdeas);
  }


  /**
   * Convert array of active objects to a list of model objects
   *
   * @param aoFedexIdeas list of active object ideas to be converted to a list of the model
   *                     FedexIdea
   * @return List<FedexIdea>
   */
  private List<FedexIdea> asListFedexIdea(AoFedexIdea[] aoFedexIdeas) {
    return Arrays.stream(aoFedexIdeas).map(this::asFedexIdea).collect(Collectors.toList());
  }


  /**
   * Convert a user key ID to the users name
   *
   * @param userKey string of the user key id
   * @return userName string
   */
  private ConfluenceUser getUsername(String userKey) {
    if (userKey != null) {
      return this.userAccessor.getUserByKey(new UserKey(userKey));
    }

    return null;
  }

  /**
   * Prepare fedex active object with the data from a fedex idea
   *
   * @param aoFedexIdea active object
   * @param fedexIdea   with data to be added to the active object
   */
  private void prepareAOFedexIdea(AoFedexIdea aoFedexIdea, FedexIdea fedexIdea) {
    aoFedexIdea.setContentId(fedexIdea.getContentId().asLong());
    aoFedexIdea.setCreatorUserKey(fedexIdea.getCreator().getKey().toString());
    aoFedexIdea.setTitle(fedexIdea.getTitle());
    aoFedexIdea.setFormData(fedexIdea.getFormData());
  }


  /**
   * Convert fedex idea active object to a fedex idea model object
   *
   * @param aoFedexIdea active object to be converted
   * @return FedexIdea object
   */
  private FedexIdea asFedexIdea(AoFedexIdea aoFedexIdea) {
    try {
      return new FedexIdea.Builder()
        .withGlobalId(aoFedexIdea.getGlobalId())
        .withTitle(aoFedexIdea.getTitle())
        .withContentId(this.pageService.getIdPageLocator(aoFedexIdea.getContentId()).getPage().getContentId())
        .withCreator(this.getUsername(aoFedexIdea.getCreatorUserKey()))
        .withFormData(aoFedexIdea.getFormData())
        .build();
    } catch (NullPointerException nullPointerException){
      return new FedexIdea.Builder().build();
    }
  }





}
