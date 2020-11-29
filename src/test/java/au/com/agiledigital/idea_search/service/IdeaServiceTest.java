package au.com.agiledigital.idea_search.service;

import static org.junit.Assert.assertEquals;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;

public class IdeaServiceTest {

  private FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
  FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);

  @Test
  public void emptyTech() {
    List<String> expected = Collections.emptyList();

    // Given dao returns an empty list.
    Mockito.when(fedexIdeaDao.queryTechDaoList()).thenReturn(expected);

    // When we call the service function to retrieve a list of technologies.
    List<String> techs = ideaService.queryTechList();

    // Then we should get an empty list.
    assertEquals(expected, techs);
  }

  @Test
  public void singleTech() {
    List<String> expected = Arrays.asList("perl");

    // Given dao returns an single technology.
    Mockito.when(fedexIdeaDao.queryTechDaoList()).thenReturn(expected);

    // When we call the service function to retrieve a list of technologies.
    List<String> techs = ideaService.queryTechList();

    // Then we should get a list with a single technology.
    assertEquals(expected, techs);
  }

  @Test
  public void multipleTech() {
    List<String> expected = Arrays.asList("perl", "python", "ts");

    // Given dao returns multiple technologies.
    Mockito.when(fedexIdeaDao.queryTechDaoList()).thenReturn(expected);

    // When we call the service function to retrieve a list of technologies.
    List<String> techs = ideaService.queryTechList();

    // Then we should get a list with multiple technologies.
    assertEquals(expected, techs);
  }

  @Test
  public void duplicateTech() {
    List<String> expected = Arrays.asList("perl", "python", "ts");

    // Given dao returns multiple duplicated technologies.
    Mockito
      .when(fedexIdeaDao.queryTechDaoList())
      .thenReturn(Arrays.asList("perl", "perl", "python", "python", "ts"));

    // When we call the service function to retrieve a list of technologies.
    List<String> techs = ideaService.queryTechList();

    // Then we should get a list with distinct technologies.
    assertEquals(expected, techs);
  }
}
