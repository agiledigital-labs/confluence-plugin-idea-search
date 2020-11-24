package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import au.com.agiledigital.idea_search.model.FedexTechnology;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultFedexIdeaService implements FedexIdeaService {
    private final FedexIdeaDao fedexIdeaDao;

    @Autowired
    public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao) {
        this.fedexIdeaDao = fedexIdeaDao;
    }

    public FedexIdea create(FedexIdea fedexIdea) {

        return this.fedexIdeaDao.create(fedexIdea);
    }

    public List<String> techList(){
        return fedexIdeaDao.techDaoList().stream().distinct().collect(Collectors.toList());
    }
}
