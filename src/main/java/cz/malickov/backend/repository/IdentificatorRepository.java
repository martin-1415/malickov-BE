package cz.malickov.backend.repository;

import cz.malickov.backend.entity.Identificator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface IdentificatorRepository  extends JpaRepository<Identificator, Integer> { // <T, ID>

    Optional<Identificator> findById(Integer identificatorId);

    @Query("""
    SELECT i
    FROM Identificator i
    WHERE NOT EXISTS (
        SELECT c
        FROM Child c
        WHERE c.identificator = i
          AND c.active = true
    )
        """)
    List<Identificator> getFreeIdentificators();
}