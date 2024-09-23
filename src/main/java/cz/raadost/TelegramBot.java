package cz.raadost;

import cz.raadost.service.messanger.Messanger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@RequiredArgsConstructor
public class TelegramBot {
	private final Messanger messanger;

	public static void main(String[] args) {
		var context = SpringApplication.run(TelegramBot.class, args);
		TelegramBot bot = context.getBean(TelegramBot.class);
		bot.initializeBot();
	}

	public void initializeBot() {
		try {
			var botApi = new TelegramBotsApi(DefaultBotSession.class);
			botApi.registerBot(messanger);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize Telegram Bots API", e);
		}
	}
}
