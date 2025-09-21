package cz.malickov.backend.repository;


import cz.malickov.backend.entity.Child;
import cz.malickov.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChildRepository  extends JpaRepository<Child, Integer> { // <T, ID>

    @Query("SELECT c FROM Child c WHERE c.user.userId = :parentId")
    List<Child> findChildrenByParentId(@Param("parentId") Long parentId);

    Long user(User user);
}