package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.dao.FedexIdeaDao;
import au.com.agiledigital.idea_search.listener.FedexIdeaEventListener;
import au.com.agiledigital.idea_search.model.FedexIdea;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultFedexIdeaService implements FedexIdeaService {
    //    private final PageManager pageManager;
//    private final DraftManager draftManager;
    private final FedexIdeaDao fedexIdeaDao;
    private static final Logger log = LoggerFactory.getLogger(DefaultFedexIdeaService.class);

    @Autowired
    public DefaultFedexIdeaService(FedexIdeaDao fedexIdeaDao) {
//        this.pageManager = pageManager;
//        this.draftManager = draftManager;
        this.fedexIdeaDao = fedexIdeaDao;
    }

    public FedexIdea create(FedexIdea fedexIdea) {
        log.warn("This is thing:" + "WARN: Created a blueprint. \n\n\n\n\n\n -----------------------------------------------------------------------------------------------------------------------");


        return this.fedexIdeaDao.create(fedexIdea);
    }
}
