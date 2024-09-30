package cz.bstudio.service.messanger;

import static cz.bstudio.service.utils.Utils.calculateResponseTime;
import static cz.bstudio.service.utils.Utils.splitMessage;

import cz.bstudio.Application;
import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.logger.LogEntity;
import cz.bstudio.service.logger.Logger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

  protected final Bot bot;
  protected final Logger logger;
  protected final MessageHandler messageHandler;

  public TelegramBot(Bot bot, Logger logger, MessageHandler messageHandler) {
    super(new DefaultBotOptions(), bot.getBotEntity().getBotToken());
    this.bot = bot;
    this.logger = logger;
    this.messageHandler = messageHandler;
  }

  @Override
  public String getBotUsername() {
    return bot.getBotEntity().getBotName();
  }

  @Override
  public void onUpdateReceived(Update update) {
    handleIncomingRequest(update);
  }

  public LinkedList<BotResponse> handleIncomingRequest(Update update) {
    LogEntity logEntity = logger.createInitialLog(update);
    var responses = new LinkedList<BotResponse>();

    try {
      if (update.hasMessage() && update.getMessage().hasText()) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        User user = update.getMessage().getFrom();
        var isAdmin = bot.isAdmin(user.getUserName());
        LinkedList<BotResponse> userMessagesToSend;
        LinkedList<BotResponse> adminMessagesToSend = new LinkedList<>();
        LinkedList<BotResponse> messagesToSend = new LinkedList<>();

        if (isAdmin) {
          adminMessagesToSend = messageHandler.handleCustomAdminMessages(messageText, user.getUserName());
        }
        userMessagesToSend = messageHandler.handleCustomUserMessages(messageText, user);

        if (!adminMessagesToSend.isEmpty()) {
          messagesToSend.addAll(adminMessagesToSend);
        }
        if (!userMessagesToSend.isEmpty()) {
          messagesToSend.addAll(userMessagesToSend);
        }

        if (messagesToSend.isEmpty()) {
          messagesToSend.addAll(messageHandler.getUnknownCommandResponse());
        }

        for (BotResponse response : messagesToSend) {
          response.setChatId(chatId);
          responses.add(response);
          sendMessage(logEntity, response);
        }
      }
    } catch (Exception e) {
      logger.logErrorMessage(e, logEntity);
      System.out.println(e.getMessage());
    }
    return responses;
  }

  public void sendMessage(LogEntity logEntity, BotResponse response) {
    long channelToUse =
        switch (response.getChannel()) {
          case USER -> response.getChatId();
          case NOTIFICATION -> messageHandler.getNotificationChannel();
        };
    var messageText = response.getMessageBody();
    logEntity.setBotResponse(messageText);
    if (messageText.isEmpty()) {
      logger.log(logEntity);
      return;
    }

    // Split the message if it exceeds the Telegram limit
    List<String> messageParts = splitMessage(messageText);
    var disableWebPreview = response.isDisableWebPreview();
    for (String part : messageParts) {
      SendMessage message = new SendMessage();
      message.setChatId(channelToUse);
      message.setText(part);
      message.setDisableWebPagePreview(disableWebPreview);
      try {
        if (!Application.IS_DEBUG) {
          {
            execute(message);
          }
        }
        logEntity.setResponseTime(calculateResponseTime(logEntity));
        logEntity.setErrorMessage(String.valueOf(response.getStatusCode()));
        logger.log(logEntity);
      } catch (TelegramApiException e) {
        var errorMessage = e.getMessage();
        log.info(errorMessage);
        logEntity.setResponseTime(calculateResponseTime(logEntity));
        logEntity.setTelegramErrorMessage(errorMessage);
        logger.log(logEntity);
      }
    }
  }


}
