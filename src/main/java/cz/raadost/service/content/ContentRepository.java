package cz.raadost.service.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    List<ContentEntity> findByTypeAndOwner(String type,String owner);
    List<ContentEntity> findByOwner(String owner);
}
