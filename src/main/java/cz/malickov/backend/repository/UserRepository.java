package cz.malickov.backend.repository;

import cz.malickov.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> { // <User, Integer>  s cim pracuje a podle ceho hleda, co je oznaceno @Id

    Optional<User> findByEmail(String email);

}
