package au.com.agiledigital.idea_search.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.user.UserAccessor;
import net.java.ao.RawEntity;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class IdeaDaoTest {
  private AoFedexTechnology aoFedexTechnologyPerl = Mockito.mock(AoFedexTechnology.class);
  private AoFedexTechnology aoFedexTechnologyPython = Mockito.mock(AoFedexTechnology.class);

  private ActiveObjects ao = Mockito.mock(ActiveObjects.class);
  private UserAccessor userAccessor = Mockito.mock(UserAccessor.class);
  FedexIdeaDao ideaDao = new FedexIdeaDao(ao, userAccessor);

  @Test
  public void emptyDaoTech() {
    List<String> expected = Collections.emptyList();
    RawEntity[] aoTechList = new AoFedexTechnology[0];

    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);

    List<String> techs = ideaDao.techDaoList();

    assertEquals(expected, techs);
  }

  @Test
  public void singleDaoTech() {
    List<String> expected = Arrays.asList("perl");
    RawEntity[] aoTechList = new AoFedexTechnology[1];
    aoTechList[0] = aoFedexTechnologyPerl;

    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");

    List<String> techs = ideaDao.techDaoList();

    assertEquals(expected, techs);
  }

  @Test
  public void multipleDaoTech() {
    List<String> expected = Arrays.asList("perl", "python");
    RawEntity[] aoTechList = new AoFedexTechnology[2];
    aoTechList[0] = aoFedexTechnologyPerl;
    aoTechList[1] = aoFedexTechnologyPython;

    Mockito.when(ao.find(Mockito.any(), Mockito.any())).thenReturn(aoTechList);
    Mockito.when(aoFedexTechnologyPerl.getTechnology()).thenReturn("perl");
    Mockito.when(aoFedexTechnologyPython.getTechnology()).thenReturn("python");

    List<String> techs = ideaDao.techDaoList();

    assertEquals(expected, techs);
  }
}
