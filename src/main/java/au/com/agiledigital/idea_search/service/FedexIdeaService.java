package au.com.agiledigital.idea_search.service;

import au.com.agiledigital.idea_search.model.FedexIdea;
import java.util.List;

public interface FedexIdeaService {
    FedexIdea create(FedexIdea fedexIdea);
    List<String> techList();
}