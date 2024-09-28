package cz.bstudio;

import cz.bstudio.service.messanger.Messanger;
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
	private final Messanger messanger;

	public static void main(String[] args) {
		var context = SpringApplication.run(Application.class, args);
		Application bot = context.getBean(Application.class);
		bot.initializeBot();
	}

	public void initializeBot() {
		try {
			var botApi = new TelegramBotsApi(DefaultBotSession.class);
			botApi.registerBot(messanger);
		} catch (TelegramApiException e) {
			log.info(e.getMessage());
			throw new RuntimeException("Failed to initialize Telegram Bots API", e);
		}
	}
}
