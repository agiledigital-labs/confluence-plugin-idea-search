package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultFedexIdeaService implements FedexIdeaService {

  private final FedexIdeaDao fedexIdeaDao;

  @Autowired
  public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao) {
    this.fedexIdeaDao = fedexIdeaDao;
  }

  /**
   * Create a new FedexIdea
   * @param fedexIdea to be created
   * @return FedexIdea that was created
   */
  public FedexIdea create(FedexIdea fedexIdea) {
    return this.fedexIdeaDao.create(fedexIdea);
  }

  /**
   * Update an existing FedexIdea
   * @param fedexIdea to be updated
   * @param contentId of idea to be updated
   * @return FedexIdea that was updated
   */
  public FedexIdea update(FedexIdea fedexIdea, long contentId) {
    return this.fedexIdeaDao.updateByContentId(fedexIdea, contentId);
  }

  /**
   * Filter technology list from dao to avoid technology duplication
   * @return list of distinct technology strings
   */
  public List<String> techList(){
    return fedexIdeaDao.techDaoList().stream().distinct().collect(Collectors.toList());
  }
}
