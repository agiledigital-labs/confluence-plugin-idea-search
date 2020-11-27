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

  private AoFedexTechnology aoFedexTechnologyPerl = Mockito.mock(
    AoFedexTechnology.class
  );
  private AoFedexTechnology aoFedexTechnologyPython = Mockito.mock(
    AoFedexTechnology.class
  );

  private ActiveObjects ao = Mockito.mock(ActiveObjects.class);
  private UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
  private FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);

  @Test
  public void emptyDaoTech() {
    List<String> expected = Collections.emptyList();

    // Given active object query returns an empty list.
    RawEntity[] aoTechList = new AoFedexTechnology[0];
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.techDaoList();

    // Then we should get an empty list.
    assertEquals(expected, techs);
  }

  @Test
  public void singleDaoTech() {
    List<String> expected = Arrays.asList("perl");

    // Given active object query returns a single list and get technology call returns technology name.
    RawEntity[] aoTechList = new AoFedexTechnology[1];
    aoTechList[0] = aoFedexTechnologyPerl;
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.techDaoList();

    // Then we should get a list with a single technology.
    assertEquals(expected, techs);
  }

  @Test
  public void multipleDaoTech() {
    List<String> expected = Arrays.asList("perl", "python");

    // Given active object query returns a list with multiple elements and get technology call returns technology name.
    RawEntity[] aoTechList = new AoFedexTechnology[2];
    aoTechList[0] = aoFedexTechnologyPerl;
    aoTechList[1] = aoFedexTechnologyPython;
    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");
    Mockito.when(aoFedexTechnologyPython.getTechnology()).thenReturn("python");

    // When we call the dao function to retrieve a list of technologies.
    List<String> techs = ideaDao.techDaoList();

    // Then we should get a list with multiple technologies.
    assertEquals(expected, techs);
  }
}
