package cz.bstudio.service.logger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cz.bstudio.service.Utils.calculateResponseTime;

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
    public void logErrorMessage(Exception e, LogEntity logEntity){
        logEntity.setErrorMessage(e.getMessage());
        logEntity.setResponseTime(calculateResponseTime(logEntity));
        log(logEntity);
    }

}
