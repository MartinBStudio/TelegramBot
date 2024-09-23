package cz.raadost.dataSource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    List<ContentEntity> findByType(String type);
}
