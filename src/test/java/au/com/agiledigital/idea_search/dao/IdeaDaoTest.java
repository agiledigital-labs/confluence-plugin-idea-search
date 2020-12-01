package au.com.agiledigital.idea_search.dao;

import static org.junit.Assert.assertEquals;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.user.UserAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.java.ao.RawEntity;
import org.junit.Test;
import org.mockito.Mockito;

public class IdeaDaoTest {

  /*
   * Isolating test cases as test data specific method call mocks are required.
   */

  /**
   * Should return an empty list if there is no tech.
   * Should not do get technology call hence no error without mock.
   */
  @Test
  public void noDaoTech() {
    ActiveObjects ao = Mockito.mock(ActiveObjects.class);
    UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
    FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);
    List<String> noDaoTech = Collections.emptyList();

    // Given active object query returns an empty list.
    RawEntity[] aoTechList = new AoFedexTechnology[0];
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.queryTechDaoList();

    // Then we should get an empty list.
    assertEquals(noDaoTech, techs);
  }

  /**
   * Should return a single tech with the mocked technology.
   */
  @Test
  public void singleDaoTech() {
    ActiveObjects ao = Mockito.mock(ActiveObjects.class);
    UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
    FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);
    AoFedexTechnology aoFedexTechnologyPerl = Mockito.mock(
      AoFedexTechnology.class
    );
    List<String> singleDaoTech = Arrays.asList("perl");

    // Given active object query returns a single list and get technology call returns technology name.
    RawEntity[] aoTechList = new AoFedexTechnology[1];
    aoTechList[0] = aoFedexTechnologyPerl;
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.queryTechDaoList();

    // Then we should get a list with a single technology.
    assertEquals(singleDaoTech, techs);
  }

  /**
   * Should return multiple techs, based on get technology mocks.
   */
  @Test
  public void multipleDaoTech() {
    ActiveObjects ao = Mockito.mock(ActiveObjects.class);
    UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
    FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);
    AoFedexTechnology aoFedexTechnologyPerl = Mockito.mock(
      AoFedexTechnology.class
    );
    AoFedexTechnology aoFedexTechnologyPython = Mockito.mock(
      AoFedexTechnology.class
    );

    List<String> multipleDaoTech = Arrays.asList("perl", "python");

    // Given active object query returns a list with multiple elements and get technology call returns technology name.
    RawEntity[] aoTechList = new AoFedexTechnology[2];
    aoTechList[0] = aoFedexTechnologyPerl;
    aoTechList[1] = aoFedexTechnologyPython;
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");
    Mockito.when(aoFedexTechnologyPython.getTechnology()).thenReturn("python");

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.queryTechDaoList();

    // Then we should get a list with multiple technologies.
    assertEquals(multipleDaoTech, techs);
  }

  /**
   * Should filter out duplicate technologies.
   */
  @Test
  public void distinctDaoTech() {
    ActiveObjects ao = Mockito.mock(ActiveObjects.class);
    UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
    FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);
    AoFedexTechnology aoFedexTechnologyDuplicateJs = Mockito.mock(
      AoFedexTechnology.class
    );
    AoFedexTechnology aoFedexTechnologyJs = Mockito.mock(
      AoFedexTechnology.class
    );
    List<String> distinctDaoTech = Arrays.asList("js");

    // Given active object query returns a list with multiple elements and get technology call returns technology name.
    RawEntity[] aoTechList = new AoFedexTechnology[2];
    aoTechList[0] = aoFedexTechnologyJs;
    aoTechList[1] = aoFedexTechnologyDuplicateJs;
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyJs.getTechnology()).thenReturn("js");
    Mockito.when(aoFedexTechnologyDuplicateJs.getTechnology()).thenReturn("js");

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.queryTechDaoList();

    // Then we should get a list with multiple technologies.
    assertEquals(distinctDaoTech, techs);
  }
}
