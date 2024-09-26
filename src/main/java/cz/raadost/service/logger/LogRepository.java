package cz.raadost.service.logger;

import cz.raadost.service.owner.BotEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
