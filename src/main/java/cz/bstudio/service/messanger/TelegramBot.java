package cz.bstudio.service.messanger;

import static cz.bstudio.service.messanger.Commands.*;
import static cz.bstudio.service.utils.Utils.calculateResponseTime;

import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.content.Content;
import cz.bstudio.service.content.ContentEntity;
import cz.bstudio.service.localization.Localization;
import cz.bstudio.service.logger.LogEntity;
import cz.bstudio.service.logger.Logger;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

  protected final Content content;
  protected final Bot bot;
  protected final Localization localization;
  protected final Logger logger;

  public TelegramBot(Content content, Bot bot, Localization localization, Logger logger) {
    super(new DefaultBotOptions(), bot.getBotEntity().getBotToken());
    this.content = content;
    this.bot = bot;
    this.localization = localization;
    this.logger = logger;
  }


  @Override
  public String getBotUsername() {
    return bot.getBotEntity().getBotName();
  }

  @Override
  public void onUpdateReceived(Update update) {
    handleIncomingRequest(update);
  }

  private void handleIncomingRequest(Update update) {
    LogEntity logEntity = logger.createInitialLog(update);
    try {
      if (update.hasMessage() && update.getMessage().hasText()) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        User user = update.getMessage().getFrom();
        var isAdmin = bot.isAdmin(user.getUserName());
        var isUserCommand = false;
        var isAdminCommand = false;

        if (isAdmin) {
          isAdminCommand = handleCustomAdminMessages(messageText, chatId, logEntity, user);
        }
        isUserCommand = handleCustomUserMessages(messageText, chatId, user, logEntity);

        if (!isUserCommand && !isAdminCommand) {
          sendMessage(chatId, String.format(localization.getInvalidRequest(),content.buildContentTypesString()) , false, logEntity);
        }
      }
    } catch (Exception e) {
      logger.logErrorMessage(e, logEntity);
    }
  }

  private boolean handleCustomAdminMessages(
      String messageText, Long chatId, LogEntity log, User user) {
      switch (messageText) {
          case IS_ADMIN -> {
              sendMessage(chatId, String.valueOf(bot.isAdmin(user.getUserName())), false, log);
              return true;
          }
          case DISPLAY_BOT_DETAILS -> {
              sendMessage(chatId, bot.display(), false, log);
              return true;
          }
          case CHANGE_LANGUAGE -> {
              localization.changeLocalization();
              sendMessage(
                      chatId,
                      String.format(localization.getLanguageChanged(), localization.getLocalization()),
                      false,
                      log);
              return true;
          }
      }
      if (isRemoveCommand(messageText)) {
      var operationMessage = content.remove(getLongFromString(messageText));
      sendMessage(chatId, operationMessage, false, log);
      return true;
    }
    if (isDisplayCommand(messageText)) {
      var operationMessage = content.display(getLongFromString(messageText));
      sendMessage(chatId, operationMessage, false, log);
      return true;
    }
    if (isEditCommand(messageText)) {
      var operationMessage = content.edit(messageText);
      sendMessage(chatId, operationMessage, false, log);
      return true;
    }
    if (isUpdateBotDetailsCommand(messageText)) {
      var operationMessage = bot.edit(messageText);
      sendMessage(chatId, operationMessage, false, log);
      return true;
    }
    if (isAddCommand(messageText)) {
      var operationMessage = content.add(messageText);
      sendMessage(chatId, operationMessage, false, log);
      return true;
    }
    return false;
  }

  private boolean handleCustomUserMessages(
      String messageText, Long chatId, User user, LogEntity log) {
    if (messageText.equals(START_COMMAND)) {
      var contentSize = content.getData("").size();
      sendMessage(chatId, String.format(localization.getWelcome(), contentSize), false, log);
      sendMessage(
          chatId,
          contentSize > 0
              ? localization.getContentTypes() + content.buildContentTypesString()
              : localization.getNoAvailableContent(),
          false,
          log);
      return true;
    }
    var contentTypes = content.getContentTypes();
    for (String type : contentTypes) {
      if (messageText.equals("/" + type)) {
        sendMessage(chatId, content.buildContentListMessage(type), true, log);
        return true;
        }
    }
    if (isNumberCommand(messageText)) {
      sendMessage(chatId, content.buildSelectedContentMessage(messageText, user), true, log);
      return true;
    }
    if (isPaidCommand(messageText)) {
      var requestedData = content.findOwnedById(extractLongFromCommand(messageText, PAID_COMMAND));
      if (requestedData != null) {
        var notificationChannel = Long.parseLong(bot.getBotEntity().getNotificationChannel());
        sendMessage(notificationChannel, buildNotificationMessage(requestedData, user), false, log);
        String message =
            (user.getUserName() == null)
                ? String.format(
                    localization.getNoUsername(), bot.getBotEntity().getAdminUsers().get(0))
                : String.format(localization.getThanks(), bot.getBotEntity().getSellerName());
        sendMessage(chatId, message, false, log);
      }
      return true;
    }
    return false;
  }

  private String buildNotificationMessage(ContentEntity data, User user) {
    String username = user.getUserName();
    String message;
    String usernameDisplay = (username == null) ? "n/a" : "@" + username;
    var operatorActionMessage =
        (username == null) ? localization.getUserWillContactYou() : localization.getContactUser();
    message =
        String.format(
            localization.getNotificationDetails(),
            usernameDisplay,
            data.getId(),
            data.getName(),
            data.getPrice(),
            user.getId(),
            operatorActionMessage);
    return message;
  }

  protected void sendMessage(Long chatId, String messageText, boolean disableWebPreview, LogEntity logEntity) {
    logEntity.setBotResponse(messageText);
    if (messageText.isEmpty()) {
      logger.log(logEntity);
      return;
    }

    // Split the message if it exceeds the Telegram limit
    List<String> messageParts = splitMessage(messageText);

    for (String part : messageParts) {
      SendMessage message = new SendMessage();
      message.setChatId(chatId.toString());
      message.setText(part);
      message.setDisableWebPagePreview(disableWebPreview);
      try {
        execute(message);
        logEntity.setResponseTime(calculateResponseTime(logEntity));
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
  private List<String> splitMessage(String message) {
    final int TELEGRAM_MESSAGE_LIMIT = 4096;
    List<String> messageParts = new ArrayList<>();
    if (message.length() <= TELEGRAM_MESSAGE_LIMIT) {
      messageParts.add(message);
    } else {
      int start = 0;
      while (start < message.length()) {
        int end = Math.min(start + TELEGRAM_MESSAGE_LIMIT, message.length());
        messageParts.add(message.substring(start, end));
        start = end;
      }
    }
    return messageParts;
  }
}
