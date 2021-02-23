package au.com.agiledigital.structured_form.dao;

import au.com.agiledigital.structured_form.model.FormData;
import au.com.agiledigital.structured_form.model.FormIndex;
import au.com.agiledigital.structured_form.model.FormIndexQuery;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static au.com.agiledigital.structured_form.helpers.Utilities.asFedexIdea;
import static au.com.agiledigital.structured_form.helpers.Utilities.getUsername;

/**
 * Fedex Idea Dao
 */
@Component
public class FormDataDao {

  public static final String AND = " AND ";
  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoFormData> AO_FEDEX_IDEA_TYPE = AoFormData.class;
  private static final Class<AoFormBlueprint> AO_IDEA_BLUEPRINT_TYPE = AoFormBlueprint.class;

  @ComponentImport
  private final UserAccessor userAccessor;
  @ComponentImport
  private final PageService pageService;


  @Autowired
  public FormDataDao(ActiveObjects ao, UserAccessor userAccessor, PageService pageService) {
    this.ao = ao;
    this.userAccessor = userAccessor;
    this.pageService = pageService;
  }

  /**
   * Create new entry to represent the a FormData
   *
   * @param formData FormData model object
   * @return FormData object created in data store
   */
  public FormData createIdea(FormData formData, Set<FormIndex> indexes) {
    AoFormData aoFormData = this.ao.create(AO_FEDEX_IDEA_TYPE);

    this.prepareAOFedexIdea(aoFormData, formData);
    this.setIndexStrings(indexes, aoFormData);
    this.setIndexNumbers(indexes, aoFormData);

    aoFormData.save();

    return asFedexIdea(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  /**
   * Get the id of plugin blueprint
   *
   * @return blueprintId or an empty string if none found
   */
  public String getBlueprintId() {
    AoFormBlueprint[] blueprints = this.ao.find(AO_IDEA_BLUEPRINT_TYPE, Query.select());
    return blueprints.length == 0 ? "" : blueprints[0].getBlueprintId();
  }

  /**
   * Set the blueprint id if not current
   *
   * @param blueprintId the supplied blueprint id
   */
  public void setBlueprintId(String blueprintId) {
    AoFormBlueprint[] blueprint = this.ao
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
    AoFormBlueprint newBlueprint = this.ao.create(AO_IDEA_BLUEPRINT_TYPE);
    newBlueprint.setBlueprintId(blueprintId);
    newBlueprint.save();
  }

  /**
   * Update a FormData by the page content id
   *
   * @param formData  New FormData for data store
   * @param contentId of the page containing the idea
   * @return FormData saved to the data store
   */
  public FormData upsertByContentId(FormData formData, long contentId, Set<FormIndex> indices) {
    AoFormData[] aoFormDatas =
      this.ao.find(
        AO_FEDEX_IDEA_TYPE,
        Query.select().where("CONTENT_ID = ?", contentId)
      );

    boolean newIdea = aoFormDatas.length == 0;

    // If the title of the page already exists the create event fails and a page update event is fired on a successful save.
    // If this happens the aoFedexIdea will not exist in the data store and will need to be created.
    AoFormData aoFormData = newIdea
      ? this.ao.create(AO_FEDEX_IDEA_TYPE)
      : aoFormDatas[0];
    this.prepareAOFedexIdea(aoFormData, formData);
    this.setIndexStrings(indices, aoFormData);
    this.setIndexNumbers(indices, aoFormData);
    this.setIndexBooleans(indices, aoFormData);

    aoFormData.save();

    return asFedexIdea(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  public void updateIndexValues(AoFormData aoFormData, Set<FormIndex> indices) {
    this.setIndexStrings(indices, aoFormData);
    this.setIndexNumbers(indices, aoFormData);
    this.setIndexBooleans(indices, aoFormData);

    aoFormData.save();
    asFedexIdea(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  /**
   * Update a FormData by the page content id
   *
   * @param contentId of the page containing the idea
   * @return FormData saved to the data store
   */
  public FormData getByContentId(long contentId) {

    List<AoFormData> test = Arrays.stream(this.ao.find(
      AO_FEDEX_IDEA_TYPE,
      Query.select().where("CONTENT_ID = ?", contentId)
    )).collect(Collectors.toList());

    return test.stream().map(aoIdea -> asFedexIdea(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor))).collect(Collectors.toList()).get(0);
  }

  /**
   * List all fedex idea in the data store
   *
   * @return a list of all available FormData
   */
  public AoFormData[] findAll() {
    Query query = Query
      .select();

    return this.ao.find(AO_FEDEX_IDEA_TYPE, query);

  }

  public AoFormData[] find() {

    Query query = Query
      .select().limit(10).offset(0);

    return this.ao.find(AO_FEDEX_IDEA_TYPE, query);
  }

  public AoFormData[] find(List<FormIndexQuery> search, int offset, int limit) {
    String whereClues = StringUtils.join(search.stream().map(r -> r.getQuery().getLeft()).collect(Collectors.toList()), AND);
    Object[] whereParams = search.stream().map(r -> r.getQuery().getRight()).flatMap(Collection::stream).toArray();

    Query query = Query
      .select().where(whereClues, whereParams).limit(limit).offset(offset);

    return this.ao.find(AO_FEDEX_IDEA_TYPE, query);
  }


  public void setIndexStrings(Set<FormIndex> indices, AoFormData aoFormData) {
    indices.stream().filter(FormIndex::isString).forEach((s -> setIndexString(aoFormData, ((String) s.getValue()), s.getIndexNumber())));
  }

  private void setIndexString(AoFormData aoFormData, String value, Integer index) {
    switch (index) {
      case 0:
        aoFormData.setIndexString0(value);
        break;
      case 1:
        aoFormData.setIndexString1(value);
        break;
      case 2:
        aoFormData.setIndexString2(value);
        break;
      case 3:
        aoFormData.setIndexString3(value);
        break;
      case 4:
        aoFormData.setIndexString4(value);
        break;
      default:
        break;
    }
  }
  public void setIndexBooleans(Set<FormIndex> indices, AoFormData aoFormData) {
    indices.stream().filter(FormIndex::isBoolean).forEach((s -> setIndexBoolean(aoFormData, ((boolean) s.getValue()), s.getIndexNumber())));
  }

  private void setIndexBoolean(AoFormData aoFormData, boolean value, Integer index) {
    switch (index) {
      case 0:
        aoFormData.setIndexBoolean0(value);
        break;
      case 1:
        aoFormData.setIndexBoolean1(value);
        break;
      case 2:
        aoFormData.setIndexBoolean2(value);
        break;
      case 3:
        aoFormData.setIndexBoolean3(value);
        break;
      case 4:
        aoFormData.setIndexBoolean4(value);
        break;
      default:
        break;
    }
  }

  public void setIndexNumbers(Set<FormIndex> indices, AoFormData aoFormData) {
    indices.stream().filter(FormIndex::isNumber).forEach((s -> setIndexNumber(aoFormData, ((Double) s.getValue()), s.getIndexNumber())));
  }

  private void setIndexNumber(AoFormData aoFormData, Double value, Integer index) {
    switch (index) {
      case 0:
        aoFormData.setIndexNumber0(value);
        break;
      case 1:
        aoFormData.setIndexNumber1(value);
        break;
      case 2:
        aoFormData.setIndexNumber2(value);
        break;
      case 3:
        aoFormData.setIndexNumber3(value);
        break;
      case 4:
        aoFormData.setIndexNumber4(value);
        break;
      default:
        break;
    }
  }


  /**
   * Prepare fedex active object with the data from a fedex idea
   *
   * @param aoFormData active object
   * @param formData   with data to be added to the active object
   */
  private void prepareAOFedexIdea(AoFormData aoFormData, FormData formData) {
    aoFormData.setContentId(formData.getContentId().asLong());
    aoFormData.setCreatorUserKey(formData.getCreator().getKey().toString());
    aoFormData.setTitle(formData.getTitle());
    aoFormData.setFormData(formData.getFormDataValue());
  }


}
