package cz.malickov.backend.repository;


import cz.malickov.backend.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ChildRepository  extends JpaRepository<Child, UUID> { // <T, ID>

    @Query("SELECT c FROM Child c WHERE c.user.userUuid = :parentUuid AND c.active=true")
    List<Child> findActiveChildrenByParentUuid(@Param("parentUuid") UUID parentUuid);

    List<Child> findByActiveTrueOrderByLastNameAsc();

    List<Child> findByActiveFalseOrderByLastNameAsc();
}