package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class ideaServiceTest {
  private FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
  FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);

  @Test
  public void emptyTech() {
    List<String> expected = Collections.emptyList();
    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(expected);

    List<String> techs = ideaService.techList();

    assertEquals(expected, techs);
  }

  @Test
  public void singleTech() {
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    List<String> expected = Arrays.asList("perl");
    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(expected);

    List<String> techs = ideaService.techList();

    assertEquals(expected, techs);
  }

  @Test
  public void multipleTech() {

    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    List<String> expected = Arrays.asList("perl", "python", "ts");
    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(expected);

    List<String> techs = ideaService.techList();

    assertEquals(expected, techs);
  }

  @Test
  public void duplicateTech() {
    FedexIdeaService ideaService = new DefaultFedexIdeaService(fedexIdeaDao);
    Mockito.when(fedexIdeaDao.techDaoList()).thenReturn(Arrays.asList("perl", "perl", "python", "python", "ts"));

    List<String> techs = ideaService.techList();
    List<String> expected = Arrays.asList("perl", "python", "ts");

    assertEquals(expected, techs);
  }
}
