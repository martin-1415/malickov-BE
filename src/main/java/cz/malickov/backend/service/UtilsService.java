package cz.malickov.backend.service;


import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.repository.IdentifierRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilsService {
    private final IdentifierRepository identifierRepository;

    public UtilsService(IdentifierRepository identifierRepository) {
        this.identifierRepository = identifierRepository;
    }

    public List<Identificator> getFreeIdentificators(){
        return this.identifierRepository.getFreeIdentificators();
    }

}
