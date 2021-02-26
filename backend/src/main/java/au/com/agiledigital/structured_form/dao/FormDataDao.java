package au.com.agiledigital.structured_form.dao;

import au.com.agiledigital.structured_form.helpers.Utilities;
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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static au.com.agiledigital.structured_form.helpers.Utilities.asFormData;
import static au.com.agiledigital.structured_form.helpers.Utilities.getUsername;

/**
 * Form Data  Dao
 * <p>
 * Access methods for the data saved in the forms
 */
@Component
public class FormDataDao {

  public static final String AND = " AND ";
  @ComponentImport
  private final ActiveObjects ao;

  private static final Class<AoFormData> AO_FORM_TYPE = AoFormData.class;
  private static final Class<AoFormBlueprint> AO_FORM_BLUEPRINT_TYPE = AoFormBlueprint.class;

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
  public FormData createIdea(@Nonnull FormData formData, @Nonnull Set<FormIndex> indices) {
    AoFormData aoFormData = this.ao.create(AO_FORM_TYPE);

    this.prepareAOFormData(aoFormData, formData);
    this.setIndex(indices, aoFormData, FormIndex::isString, this.setString);
    this.setIndex(indices, aoFormData, FormIndex::isBoolean, this.setBoolean);
    this.setIndex(indices, aoFormData, FormIndex::isNumber, this.setNumber);

    aoFormData.save();

    return Utilities.asFormData(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  /**
   * Get the id of plugin blueprint
   *
   * @return blueprintId or an empty string if none found
   */
  @Nonnull
  public String getBlueprintId() {
    AoFormBlueprint[] blueprints = this.ao.find(AO_FORM_BLUEPRINT_TYPE, Query.select());
    return blueprints.length == 0 ? "" : blueprints[0].getBlueprintId();
  }

  /**
   * Set the blueprint id if not current
   *
   * @param blueprintId the supplied blueprint id
   */
  public void setBlueprintId(String blueprintId) {
    AoFormBlueprint[] blueprint = this.ao
      .find(AO_FORM_BLUEPRINT_TYPE, Query.select().where("BLUEPRINT_ID = ?", blueprintId));

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
    AoFormBlueprint newBlueprint = this.ao.create(AO_FORM_BLUEPRINT_TYPE);
    newBlueprint.setBlueprintId(blueprintId);
    newBlueprint.save();
  }

  /**
   * Update a FormData by the page content id
   *
   * @param formData  New FormData for data store
   * @param contentId of the page containing the data
   * @return FormData saved to the data store
   */
  public FormData upsertByContentId(@Nonnull FormData formData, long contentId, @Nonnull Set<FormIndex> indices) {
    AoFormData[] aoFormDatas =
      this.ao.find(
        AO_FORM_TYPE,
        Query.select().where("CONTENT_ID = ?", contentId)
      );

    boolean newIdea = aoFormDatas.length == 0;

    // If the title of the page already exists the create event fails and a page update event is fired on a successful save.
    // If this happens the aoFormData will not exist in the data store and will need to be created.
    AoFormData aoFormData = newIdea
      ? this.ao.create(AO_FORM_TYPE)
      : aoFormDatas[0];
    this.prepareAOFormData(aoFormData, formData);

    // extract the indexable data from the json payload and save it in the index fields
    setIndexValues(aoFormData, indices);

//    save data
    aoFormData.save();

    return Utilities.asFormData(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  /**
   * Update the values of the index items based on the the new index schema
   *
   * @param aoFormData object have the indexes updated
   * @param indices    set to be updated on the form data
   */
  public void updateIndexValues(@Nonnull AoFormData aoFormData, @Nonnull Set<FormIndex> indices) {
    setIndexValues(aoFormData, indices);

    aoFormData.save();
    Utilities.asFormData(aoFormData, this.pageService, getUsername(aoFormData.getCreatorUserKey(), this.userAccessor));
  }

  /**
   * Helper method to set the 3 different types of indices
   *
   * @param aoFormData object have the indexes updated
   * @param indices    set to be updated on the form data
   */
  private void setIndexValues(@Nonnull AoFormData aoFormData, @Nonnull Set<FormIndex> indices) {
    this.setIndex(indices, aoFormData, FormIndex::isString, this.setString);
    this.setIndex(indices, aoFormData, FormIndex::isBoolean, this.setBoolean);
    this.setIndex(indices, aoFormData, FormIndex::isNumber, this.setNumber);
  }

  /**
   * Get a FormData object by the page content id
   *
   * @param contentId of the page containing the data
   * @return FormData saved to the data store
   */
  public FormData getByContentId(long contentId) {

    List<AoFormData> aoFormDataList = Arrays.stream(this.ao.find(
      AO_FORM_TYPE,
      Query.select().where("CONTENT_ID = ?", contentId)
    )).collect(Collectors.toList());

    return aoFormDataList.stream().map(aoIdea -> Utilities.asFormData(aoIdea, this.pageService, getUsername(aoIdea.getCreatorUserKey(), this.userAccessor))).collect(Collectors.toList()).get(0);
  }

  /**
   * List all form datas in the data store
   *
   * @return a list of all available AoFormData
   */
  public AoFormData[] findAll() {
    Query query = Query
      .select();

    return this.ao.find(AO_FORM_TYPE, query);

  }

  /**
   * Find the first 10 AoFormData
   *
   * @return an array of all available AoFormData
   */
  public AoFormData[] find() {

    Query query = Query
      .select().limit(10).offset(0);

    return this.ao.find(AO_FORM_TYPE, query);
  }

  /**
   * Search for AoFormData based on list of FormIndexQuery,
   * Limit or offset the search for pagination
   *
   * @param search list of FormIndexQuery use in the search
   * @param offset for the sql query
   * @param limit limit the number of results returned
   * @return  an array of all available AoFormData
   */
  public AoFormData[] find(@Nonnull List<FormIndexQuery> search, int offset, int limit) {
    String whereClues = StringUtils.join(search.stream().map(r -> r.getQuery().getLeft()).collect(Collectors.toList()), AND);
    Object[] whereParams = search.stream().map(r -> r.getQuery().getRight()).flatMap(Collection::stream).toArray();

    Query query = Query
      .select().where(whereClues, whereParams).limit(limit).offset(offset);

    return this.ao.find(AO_FORM_TYPE, query);
  }

  /**
   * Filter FormIndexes and set the value in the AoFormData object
   *
   * @param indices set to be added to the AoFormData
   * @param aoFormData object to be modified
   * @param filter method to filter the set of indexes
   * @param applyFunction function to apply the indexes to the ao object
   */
  public void setIndex(@Nonnull Set<FormIndex> indices, @Nonnull AoFormData aoFormData, Predicate<FormIndex> filter, Function<AoFormData, Consumer<FormIndex>> applyFunction) {
    indices.stream().filter(filter).filter(r -> r.getValue() != null).forEach(applyFunction.apply(aoFormData));
  }

  /**
   * Helper function to construct the consumer to apply changes to the string indexes
   */
  private final Function<AoFormData, Consumer<FormIndex>> setString = aoFormData -> formIndex -> this.setIndexString(aoFormData,formIndex);

  /**
   * Sets the index value in the ao object
   *
   * @param aoFormData object to be modified
   * @param formIndex object containing value
   */
  private void setIndexString(@Nonnull AoFormData aoFormData, FormIndex formIndex) {
    switch (formIndex.getIndexNumber()) {
      case 0:
        aoFormData.setIndexString0(formIndex.getValue().toString());
        break;
      case 1:
        aoFormData.setIndexString1(formIndex.getValue().toString());
        break;
      case 2:
        aoFormData.setIndexString2(formIndex.getValue().toString());
        break;
      case 3:
        aoFormData.setIndexString3(formIndex.getValue().toString());
        break;
      case 4:
        aoFormData.setIndexString4(formIndex.getValue().toString());
        break;
      default:
        break;
    }
  }

  /**
   * Helper function to construct the consumer to apply changes to the boolean indexes
   */
  private final Function<AoFormData, Consumer<FormIndex>> setBoolean = aoFormData -> formIndex -> this.setIndexBoolean(aoFormData,formIndex);

  /**
   * Sets the index value in the ao object
   *
   * @param aoFormData object to be modified
   * @param formIndex object containing value
   */
  private void setIndexBoolean(@Nonnull AoFormData aoFormData, FormIndex formIndex) {
    switch (formIndex.getIndexNumber()) {
      case 0:
        aoFormData.setIndexBoolean0(((boolean) formIndex.getValue()));
        break;
      case 1:
        aoFormData.setIndexBoolean1(((boolean) formIndex.getValue()));
        break;
      case 2:
        aoFormData.setIndexBoolean2(((boolean) formIndex.getValue()));
        break;
      case 3:
        aoFormData.setIndexBoolean3(((boolean) formIndex.getValue()));
        break;
      case 4:
        aoFormData.setIndexBoolean4(((boolean) formIndex.getValue()));
        break;
      default:
        break;
    }
  }

  /**
   * Helper function to construct the consumer to apply changes to the number indexes
   */
  private final Function<AoFormData, Consumer<FormIndex>> setNumber = aoFormData -> formIndex -> this.setIndexNumber(aoFormData,formIndex);

  /**
   * Sets the index value in the ao object
   *
   * @param aoFormData object to be modified
   * @param formIndex object containing value
   */
  private void setIndexNumber(@Nonnull AoFormData aoFormData, FormIndex formIndex) {
    switch (formIndex.getIndexNumber()) {
      case 0:
        aoFormData.setIndexNumber0(((double) formIndex.getValue()));
        break;
      case 1:
        aoFormData.setIndexNumber1(((double) formIndex.getValue()));
        break;
      case 2:
        aoFormData.setIndexNumber2(((double) formIndex.getValue()));
        break;
      case 3:
        aoFormData.setIndexNumber3(((double) formIndex.getValue()));
        break;
      case 4:
        aoFormData.setIndexNumber4(((double) formIndex.getValue()));
        break;
      default:
        break;
    }
  }


  /**
   * Prepare form data active object with the data from a form data
   *
   * @param aoFormData active object
   * @param formData   with data to be added to the active object
   */
  private void prepareAOFormData(@Nonnull AoFormData aoFormData, @Nonnull FormData formData) {
    aoFormData.setContentId(formData.getContentId().asLong());
    aoFormData.setCreatorUserKey(formData.getCreator().getKey().toString());
    aoFormData.setTitle(formData.getTitle());
    aoFormData.setFormData(formData.getFormDataValue());
  }


}
