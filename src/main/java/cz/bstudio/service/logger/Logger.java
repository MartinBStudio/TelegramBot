package cz.bstudio.service.logger;

import cz.bstudio.service.bot.Bot;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

import static cz.bstudio.service.utils.Utils.calculateResponseTime;

@Service
@RequiredArgsConstructor
public class Logger {
    private final LogRepository logRepository;
    private final Bot bot;

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
    @Transactional
    public void logErrorMessage(Exception e, LogEntity logEntity){
        logEntity.setErrorMessage(e.getMessage());
        logEntity.setResponseTime(calculateResponseTime(logEntity));
        System.out.println(e.getMessage());
        log(logEntity);
    }
    public LogEntity createInitialLog(Update update) {
        var message = update.getMessage();
        String messageText = message.getText();
        var messageId = message.getMessageId();
        Long chatId = message.getChatId();
        User user = message.getFrom();
        var isGroupMessage = message.isGroupMessage();
        var userId = user.getId();
        var username = user.getUserName();
        var isBot = user.getIsBot();
        var language = user.getLanguageCode();
        var isAdmin = bot.isAdmin(username);

        return LogEntity.builder()
                .botName(bot.getBotEntity().getBotName())
                .username(username)
                .userId(userId)
                .chatId(chatId)
                .isAdmin(isAdmin)
                .messageId(messageId)
                .message(messageText)
                .userLanguage(language)
                .isBot(isBot)
                .isGroupChat(isGroupMessage) // Private chat
                .timestamp(LocalDateTime.now()) // Manually setting timestamp if needed
                .build();
    }

}
