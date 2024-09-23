package cz.raadost.service.content;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    List<ContentEntity> findByType(String type);
    void deleteByContentIndex(Long contentId);
}
