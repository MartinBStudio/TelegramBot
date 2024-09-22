package cz.raadost.config;

import cz.raadost.service.Messanger;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@PropertySource("classpath:telegram.properties")
public class TelegramBotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Telegram Bots API", e);
        }
    }
    @Value("${telegram.bot.notification.channel.id}")
    private Long NOTIFICATION_CHANNEL_ID;
    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;
    @Value("${telegram.bot.username}")
    private String BOT_USERNAME;
    @Bean
    public Messanger messanger(TelegramBotsApi telegramBotsApi) {
        Messanger messanger = new Messanger(NOTIFICATION_CHANNEL_ID, BOT_TOKEN, BOT_USERNAME);
        try {
            telegramBotsApi.registerBot(messanger);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return messanger;
    }
}
