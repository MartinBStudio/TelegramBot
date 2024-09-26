package cz.raadost.service.messanger;

import static cz.raadost.service.messanger.Commands.*;

import cz.raadost.service.content.Content;
import cz.raadost.service.content.ContentEntity;
import cz.raadost.service.localization.Localization;
import cz.raadost.service.logger.LogEntity;
import cz.raadost.service.logger.Logger;
import cz.raadost.service.owner.Bot;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@PropertySource("classpath:telegram.properties")
@RequiredArgsConstructor
public class Messanger extends TelegramLongPollingBot {

  private final Content content;
  private final Bot bot;
  private final Localization localization;
  private final Logger logger;

  @Override
  public String getBotToken() {
    return bot.getBotEntity().getBotToken();
  }

  @Override
  public String getBotUsername() {
    return bot.getBotEntity().getBotName();
  }

  private LogEntity createInitialLog(Update update) {
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
    var isAdmin = isAdmin(username);

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

  private Long calculateResponseTime(LogEntity logEntity) {
    var initialTime = logEntity.getTimestamp();
    var finalizedTime = LocalDateTime.now();
    long responseTimeInMillis = Duration.between(initialTime, finalizedTime).toMillis();
    return responseTimeInMillis;
  }

  @Override
  public void onUpdateReceived(Update update) {
    LogEntity logEntity = createInitialLog(update);
    try {
      if (update.hasMessage() && update.getMessage().hasText()) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        User user = update.getMessage().getFrom();
        // ADMIN
        if (isAdmin(user.getUserName())) {
          handleCustomAdminMessages(messageText, chatId, logEntity,user);
        }
        // ANY USER
        switch (messageText) {
          case START_COMMAND:
            var contentSize = content.getData("").size();
            sendMessage(chatId, String.format(localization.getWelcome(), contentSize), logEntity);
            sendMessage(
                chatId,
                contentSize > 0
                    ? localization.getContentTypes()
                    : localization.getNoAvailableContent(),
                logEntity);
            break;
          case ALL_COMMAND:
            sendContentListMessage(chatId, "", logEntity);
            break;
          case VIDEO_COMMAND:
            sendContentListMessage(chatId, "Video", logEntity);
            break;
          case SPECIAL_COMMAND:
            sendContentListMessage(chatId, "Special", logEntity);
            break;
          case BUNDLE_COMMAND:
            sendContentListMessage(chatId, "Bundle", logEntity);
            break;
          case PHOTOS_COMMAND:
            sendContentListMessage(chatId, "Photo", logEntity);
            break;
          default:
            handleCustomUserMessages(messageText, chatId, user, logEntity);
        }
      }
    } catch (Exception e) {
      logEntity.setErrorMessage(e.getMessage());
      logEntity.setResponseTime(calculateResponseTime(logEntity));
      logger.log(logEntity);
    }
  }

  private boolean isAdmin(String userName) {
    return bot.getBotEntity().getAdminUsers().contains(userName);
  }

  // ADMIN
  private void handleCustomAdminMessages(String messageText, Long chatId, LogEntity log,User user) {
    if(messageText.equals(IS_ADMIN)){
    sendMessage(chatId, String.valueOf(isAdmin(user.getUserName())), log);
    return;
    }
    if (messageText.equals(DISPLAY_BOT_DETAILS)) {
      sendMessage(chatId, bot.display(), log);
      return;
    }
    if (messageText.equals(CHANGE_LANGUAGE)) {
      localization.changeLocalization();
      sendMessage(chatId, "Bot language switched to - " + localization.getLocalization(), log);
      return;
    }
    if (isRemoveCommand(messageText)) {
      var operationMessage = content.remove(getLongFromString(messageText));
      sendMessage(chatId, operationMessage, log);
      return;
    }
    if (isDisplayCommand(messageText)) {
      var operationMessage = content.display(getLongFromString(messageText));
      sendMessage(chatId, operationMessage, log);
      return;
    }
    if (isEditCommand(messageText)) {
      var operationMessage = content.edit(messageText);
      sendMessage(chatId, operationMessage, log);
      return;
    }
    if (isUpdateBotDetailsCommand(messageText)) {
      var operationMessage = bot.edit(messageText);
      sendMessage(chatId, operationMessage, log);
      return;
    }
    if (isAddCommand(messageText)) {
      var operationMessage = content.add(messageText);
      sendMessage(chatId, operationMessage, log);
      return;
    }

  }

  // USER
  private void handleCustomUserMessages(String messageText, Long chatId, User user, LogEntity log) {
    if (isNumberCommand(messageText)) {
      sendSpecificContentMessage(messageText, chatId, user, log);
      return;
    }
    if (isPaidCommand(messageText)) {
      sendUserPaidMessage(messageText, chatId, user, log);
      return;
    }
    if (!isAdmin(user.getUserName())) {
      sendMessage(chatId, localization.getInvalidRequest(), log);
      log.setResponseTime(calculateResponseTime(log));
      logger.log(log);
    }
  }

  private void sendSpecificContentMessage(
      String messageText, Long chatId, User user, LogEntity log) {
    var messageNumber = extractLongFromCommand(messageText, NUMBER_COMMAND);
    if (content.findById(messageNumber) != null) {
      sendMessage(
          chatId,
          buildContentMessageFromStringIndex(String.valueOf(messageNumber), user.getId()),
          true,
          log);
    } else {
      sendMessage(chatId, localization.getContentOutOfBounds(), log);
    }
  }

  private void sendUserPaidMessage(String messageText, Long chatId, User user, LogEntity log) {
    var requestedData = content.findById(extractLongFromCommand(messageText, PAID_COMMAND));
    if (requestedData != null) {
      var data = requestedData;
      String username = user.getUserName();
      var operatorActionMessage = localization.getContactUser();
      if (username == null) {
        var userToContact = bot.getBotEntity().getAdminUsers().get(0);
        var message = String.format(localization.getNoUsername(), userToContact);
        sendMessage(chatId, message, log);
        operatorActionMessage = localization.getUserWillContactYou();
      } else {
        sendMessage(
            chatId,
            String.format(localization.getThanks(), bot.getBotEntity().getSellerName()),
            log);
      }
      String usernameDisplay = (username == null) ? "nemá vypněné" : "@" + username;
      String channelMessageText =
          String.format(
              localization.getNotificationDetails(),
              usernameDisplay,
              data.getId(),
              data.getName(),
              data.getPrice(),
              user.getId(),
              operatorActionMessage);
      sendMessage(
          Long.parseLong(bot.getBotEntity().getNotificationChannel()), channelMessageText, log);
    }
  }

  private void sendContentListMessage(Long chatId, String filter, LogEntity log) {
    List<ContentEntity> content = this.content.getData(filter);
    if (content.size() > 0) {
      int batchSize = 30; // Maximum number of items per message
      List<List<ContentEntity>> batches = new ArrayList<>();
      // Split the content into batches
      for (int i = 0; i < content.size(); i += batchSize) {
        int end = Math.min(i + batchSize, content.size());
        batches.add(content.subList(i, end));
      }
      // Send each batch as a separate message
      for (List<ContentEntity> batch : batches) {
        StringBuilder sb = new StringBuilder();
        for (ContentEntity data : batch) {
          sb.append(
              String.format("/%s - %s - %s CZK\n", data.getId(), data.getName(), data.getPrice()));
        }
        if (!sb.isEmpty()) sendMessage(chatId, sb.toString(), log);
      }
    } else {
      sendMessage(chatId, "No content found.", log);
    }
  }

  private String buildContentMessageFromStringIndex(String index, Long userId) {
    var botEntity = bot.getBotEntity();
    var selectedData = content.findById(Long.parseLong(index));
    var contentSelected = localization.getContentSelected();
    var contentName = selectedData.getName();
    var contentType = selectedData.getType();
    var contentDescription = selectedData.getDescription();
    var contentPrice = selectedData.getPrice();
    var payment1 = botEntity.getPaymentMethod1();
    var payment2 = botEntity.getPaymentMethod2();
    var paymentGuide = localization.getPaymentGuide();

    var paymentCommand = "/ZAPLACENO_" + selectedData.getId();

    return String.format(
        localization.getContentDetails(),
        contentSelected,
        contentName,
        contentType,
        contentDescription,
        contentPrice,
        payment1,
        payment2,
        userId,
        paymentGuide,
        paymentCommand);
  }

  // ADMIN
  private void sendMessage(
      Long chatId, String messageText, boolean disableWebPreview, LogEntity log) {
    log.setBotResponse(messageText);
    if (messageText.isEmpty()) {
      logger.log(log);
      return;
    }
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());
    message.setText(messageText);
    message.setDisableWebPagePreview(disableWebPreview);
    executeMessage(log, message);
  }

  private void executeMessage(LogEntity log, SendMessage sendMessage) {
    try {
      execute(sendMessage);
      log.setResponseTime(calculateResponseTime(log));
      logger.log(log);
    } catch (TelegramApiException e) {
      e.printStackTrace();
      log.setResponseTime(calculateResponseTime(log));
      log.setTelegramErrorMessage(e.getMessage());
      logger.log(log);
    }
  }

  private void sendMessage(Long chatId, String messageText, LogEntity log) {
    log.setBotResponse(messageText);
    if (messageText.isEmpty()) {
      log.setResponseTime(calculateResponseTime(log));
      logger.log(log);
      return;
    }
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());
    message.setText(messageText);
    executeMessage(log, message);
  }
}
