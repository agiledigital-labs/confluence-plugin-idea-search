package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.model.FedexIdea;
import org.springframework.beans.factory.annotation.Autowired;


public class DefaultFedexIdeaService implements FedexIdeaService {
    private final FedexIdeaDao fedexIdeaDao;

    @Autowired
    public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao) {
        this.fedexIdeaDao = fedexIdeaDao;
    }

    public FedexIdea create(FedexIdea fedexIdea) {

        return this.fedexIdeaDao.create(fedexIdea);
    }
}
