package cz.bstudio;

import cz.bstudio.service.messanger.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Application {

	private final TelegramBot telegramBot;

	public static void main(String[] args) {
		var context = SpringApplication.run(Application.class, args);
		Application botApp = context.getBean(Application.class);
		botApp.initializeBot();
	}

	public void initializeBot() {
		try {
			TelegramBotsApi botApi = new TelegramBotsApi(DefaultBotSession.class);
			botApi.registerBot(telegramBot);
			log.info("Telegram bot initialized successfully.");
		} catch (TelegramApiException e) {
			log.error("Failed to initialize Telegram Bots API: {}", e.getMessage());
			throw new RuntimeException("Error initializing Telegram Bots API", e);
		}
	}
}

