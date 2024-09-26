package cz.bstudio.service.bot;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<BotEntity, Long> {
    Optional<BotEntity> findBotEntityByBotName(String type);
}
