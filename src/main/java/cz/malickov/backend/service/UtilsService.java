package cz.malickov.backend.service;


import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.repository.IdentificatorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilsService {
    private final IdentificatorRepository identificatorRepository;

    public UtilsService(IdentificatorRepository identificatorRepository) {
        this.identificatorRepository = identificatorRepository;
    }

    public List<Identificator> getFreeIdentificators(){
        return this.identificatorRepository.getFreeIdentificators();
    }

}
