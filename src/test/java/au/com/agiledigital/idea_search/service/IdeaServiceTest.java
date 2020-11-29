package au.com.agiledigital.idea_search.service;

import static org.junit.Assert.assertEquals;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

@RunWith(Parameterized.class)
public class IdeaServiceTest {

  private FedexIdeaDao fedexIdeaDao = Mockito.mock(FedexIdeaDao.class);
  private FedexIdeaService ideaService = new DefaultFedexIdeaService(
    fedexIdeaDao
  );

  private static List<String> noTech = Collections.emptyList();
  private static List<String> singleTech = Arrays.asList("perl");
  private static List<String> multipleTech = Arrays.asList(
    "perl",
    "python",
    "ts"
  );

  private final List<String> supplied;

  public IdeaServiceTest(List<String> supplied) {
    this.supplied = supplied;
  }

  @Parameters(name = "{index}: Pass through the list unchanged returned from dao {0}")
  public static Object[] data() {
    return new Object[] { noTech, singleTech, multipleTech };
  }

  @Test
  public void relayTechListFromDao() {
    Mockito.when(fedexIdeaDao.queryTechDaoList()).thenReturn(supplied);

    assertEquals(ideaService.queryTechList(), supplied);
  }
}
