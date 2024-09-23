package cz.raadost.service.messanger;

import static cz.raadost.service.messanger.Commands.*;

import cz.raadost.service.content.Content;
import cz.raadost.service.content.ContentEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@PropertySource("classpath:telegram.properties")
@RequiredArgsConstructor
public class Messanger extends TelegramLongPollingBot {

  private final Content content;

  @Value("${telegram.bot.notification.channel.id}")
  private Long NOTIFICATION_CHANNEL_ID;

  @Value("${telegram.bot.token}")
  private String BOT_TOKEN;

  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;

  @Override
  public String getBotToken() {
    return BOT_TOKEN;
  }

  @Override
  public String getBotUsername() {
    return BOT_USERNAME;
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      Long chatId = update.getMessage().getChatId();
      User user = update.getMessage().getFrom();

      switch (messageText) {
        case START_COMMAND:
          sendMessage(chatId, StaticMessages.WELCOME.getMessage());
          break;

        case ALL_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          buildContentListMessage(chatId, "");
          break;
        case VIDEO_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          buildContentListMessage(chatId, "Video");
          break;
        case SPECIAL_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          buildContentListMessage(chatId, "Special");
          break;
        case BUNDLE_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          buildContentListMessage(chatId, "Bundle");
          break;
        default:
          handleCustomMessages(messageText, chatId, user);
          break;
      }
    }
  }

  private void handleCustomMessages(String messageText, Long chatId, User user) {
    if (isNumberCommand(messageText)) {
      handleNumberMessage(messageText, chatId, user);
      return;
    }
    if (isPaidCommand(messageText)) {
      handleUserPaidMessage(messageText, chatId, user);
      return;
    }
    sendMessage(chatId, StaticMessages.INVALID_REQUEST.getMessage());
  }


  private void handleNumberMessage(String messageText, Long chatId, User user) {
    int messageNumber = getNumberFromString(messageText);
    if (content.findById(messageNumber) != null) {
      sendMessage(
          chatId, buildContentMessageFromStringIndex(String.valueOf(messageNumber), user.getId()));
    } else {
      sendMessage(chatId, StaticMessages.CONTENT_OUT_OF_BOUNDS.getMessage());
    }
  }

  private void handleUserPaidMessage(String messageText, Long chatId, User user) {
    var requestedData = content.findById(getNumberFromString(messageText));
    if (requestedData != null) {
      var data = requestedData;
      String username = user.getUserName();
      var operatorActionMessage = StaticMessages.CONTACT_USER.getMessage();
      if (username == null) {
        sendMessage(chatId, StaticMessages.NO_USERNAME_MESSAGE.getMessage());
        operatorActionMessage = StaticMessages.USER_WILL_CONTACT_YOU.getMessage();
      } else {
        sendMessage(chatId, StaticMessages.THANKS_MESSAGE.getMessage());
      }
      String usernameDisplay = (username == null) ? "nemá vypněné" : "@" + username;
      String channelMessageText =
          String.format(
              "Username - %s\nObsah - [%s] %s\nČástka - %sCZK\nPoznámka k platbě - %s\n\n%s",
              usernameDisplay,
              data.getContentIndex(),
              data.getName(),
              data.getPrice(),
              user.getId(),
              operatorActionMessage);
      sendMessage(NOTIFICATION_CHANNEL_ID, channelMessageText);
    }
  }

  private void buildContentListMessage(Long chatId, String filter) {
    List<ContentEntity> content = this.content.getData(filter);
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
            String.format(
                "/%s - %s - %s CZK\n", data.getContentIndex(), data.getName(), data.getPrice()));
      }
      if (!sb.isEmpty()) sendMessage(chatId, sb.toString());
    }
  }

  private String buildContentMessageFromStringIndex(String index, Long userId) {
    var selectedData = content.findById(Long.parseLong(index));
    var contentSelected = StaticMessages.CONTENT_SELECTED.getMessage();

    var contentName = selectedData.getName();
    var contentType = selectedData.getType();
    var contentDescription = selectedData.getDescription();
    var contentPrice = selectedData.getPrice();
    var paymentDetails = StaticMessages.PAYMENT_DETAILS.getMessage();
    var paymentGuide = StaticMessages.PAYMENT_GUIDE.getMessage();

    var paymentCommand = "/ZAPLACENO_" + selectedData.getContentIndex();

    return String.format(
        " %s\n\n%s\n DRUH - %s\n POPIS - %s\n CENA - %sCZK\n\n %s %s\n\n%s%s",
        contentSelected,
        contentName,
        contentType,
        contentDescription,
        contentPrice,
        paymentDetails,
        userId,
        paymentGuide,
        paymentCommand);
  }

  private void sendMessage(Long chatId, String messageText) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());
    message.setText(messageText);

    try {
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
