package cz.bstudio.service.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    List<ContentEntity> findByTypeAndOwner(String type,String owner);
    List<ContentEntity> findByOwner(String owner);
    @Query("SELECT DISTINCT c.type FROM ContentEntity c WHERE c.owner = :owner")
    List<String> findDistinctContentTypesByOwner(@Param("owner") String owner);
}
