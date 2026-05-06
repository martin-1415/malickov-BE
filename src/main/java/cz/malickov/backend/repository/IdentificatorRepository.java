package cz.malickov.backend.repository;

import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.Identificator;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IdentificatorRepository  extends JpaRepository<Identificator, Integer> { // <T, ID>

    Optional<Identificator> findById(Integer identificatorId);
}