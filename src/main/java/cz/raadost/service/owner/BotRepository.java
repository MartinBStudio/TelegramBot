package cz.raadost.service.owner;

import cz.raadost.service.content.ContentEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<BotEntity, Long> {
    Optional<BotEntity> findBotEntityByBotName(String type);
}
