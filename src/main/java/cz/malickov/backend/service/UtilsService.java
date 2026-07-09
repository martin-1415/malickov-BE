package cz.malickov.backend.service;


import cz.malickov.backend.entity.Holiday;
import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.repository.HolidayRepository;
import cz.malickov.backend.repository.IdentifierRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilsService {
    private final IdentifierRepository identifierRepository;
    private final HolidayRepository holidayRepository;

    public UtilsService(IdentifierRepository identifierRepository,
                        HolidayRepository holidayRepository) {
        this.identifierRepository = identifierRepository;
        this.holidayRepository = holidayRepository;
    }

    public List<Identificator> getFreeIdentificators(){
        return this.identifierRepository.getFreeIdentificators();
    }

    public List<Holiday> getHolidays(){
        return this.holidayRepository.findAll();
    }

}
