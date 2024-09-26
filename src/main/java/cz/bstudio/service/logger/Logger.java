package cz.bstudio.service.logger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Logger {
    private final LogRepository logRepository;

    @Transactional
    public void log(LogEntity logEntity) {
        var botResponse = logEntity.getBotResponse();
        if(botResponse != null) {
            if(botResponse.length()>1500){
                logEntity.setBotResponse(botResponse.substring(0, 1500));
            }
        }
        logRepository.save(logEntity);
    }
}
