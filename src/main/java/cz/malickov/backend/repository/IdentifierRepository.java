package cz.malickov.backend.repository;

import cz.malickov.backend.entity.Identificator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface IdentifierRepository extends JpaRepository<Identificator, Integer> { // <T, ID>

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