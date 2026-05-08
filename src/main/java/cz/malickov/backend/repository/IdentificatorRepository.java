package cz.malickov.backend.repository;

import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.Identificator;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IdentificatorRepository  extends JpaRepository<Identificator, Integer> { // <T, ID>

    Optional<Identificator> findById(Integer identificatorId);

    @Query("SELECT i.* FROM identificator i WHERE NOT EXISTS(" +
                "SELECT 1 FROM child c WHERE c.identificator_id = i.identificator_id AND c.active = TRUE" +
            ");")
    List<Identificator> getFreeIdentificators();
}