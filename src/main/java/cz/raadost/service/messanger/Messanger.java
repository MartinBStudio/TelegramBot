package cz.raadost.service.messanger;

import static cz.raadost.service.messanger.Commands.*;

import cz.raadost.service.content.Content;
import cz.raadost.service.content.ContentEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  protected final Content content;

  @Value("${telegram.bot.notification.channel.id}")
  private Long NOTIFICATION_CHANNEL_ID;

  @Value("${telegram.bot.token}")
  private String BOT_TOKEN;

  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;

  @Value("#{'${telegram.bot.admin.users.list}'.split(',')}")
  private List<String> adminUsers;

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
      // ADMIN
      if (isAdmin(user.getUserName())) {
        handleCustomAdminMessages(messageText, chatId, user);
      }
      // ANY USER
      switch (messageText) {
        case IS_ADMIN:
          sendMessage(chatId, String.valueOf(isAdmin(user.getUserName())));
          break;
        case START_COMMAND:
          sendMessage(chatId, StaticMessages.WELCOME.getMessage());
          break;
        case ALL_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          sendContentListMessage(chatId, "");
          break;
        case VIDEO_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          sendContentListMessage(chatId, "Video");
          break;
        case SPECIAL_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          sendContentListMessage(chatId, "Special");
          break;
        case BUNDLE_COMMAND:
          sendMessage(chatId, StaticMessages.PICK_CONTENT.getMessage());
          sendContentListMessage(chatId, "Bundle");
          break;
        default:
          handleCustomUserMessages(messageText, chatId, user);
          break;
      }
    }
  }
  private boolean isAdmin(String userName) {
    return adminUsers.contains(userName);
  }
  //ADMIN
  private void handleCustomAdminMessages(String messageText, Long chatId, User user) {
    if (isRemoveCommand(messageText)) {
      var operationMessage = content.remove(getLongFromString(messageText));
      sendMessage(chatId, operationMessage);
      return;
    }
    if (isDisplayCommand(messageText)) {
      var operationMessage = content.display(getLongFromString(messageText));
      sendMessage(chatId, operationMessage);
      return;
    }
    if (isEditCommand(messageText)) {
      var operationMessage = content.edit(messageText);
      sendMessage(chatId,operationMessage);
      return;
    }
    if (isAddCommand(messageText)) {
      var operationMessage = content.add(messageText);
      sendMessage(chatId,operationMessage);
      return;
    }
  }

  // USER
  private void handleCustomUserMessages(String messageText, Long chatId, User user) {
    if (isNumberCommand(messageText)) {
      sendSpecificContentMessage(messageText, chatId, user);
      return;
    }
    if (isPaidCommand(messageText)) {
      sendUserPaidMessage(messageText, chatId, user);
      return;
    }
    if(!isAdmin(user.getUserName())) {
    sendMessage(chatId, StaticMessages.INVALID_REQUEST.getMessage());
    }
  }

  private void sendSpecificContentMessage(String messageText, Long chatId, User user) {
    var messageNumber = extractLongFromCommand(messageText,NUMBER_COMMAND);
    if (content.findById(messageNumber) != null) {
      sendMessage(
          chatId, buildContentMessageFromStringIndex(String.valueOf(messageNumber), user.getId()));
    } else {
      sendMessage(chatId, StaticMessages.CONTENT_OUT_OF_BOUNDS.getMessage());
    }
  }

  private void sendUserPaidMessage(String messageText, Long chatId, User user) {
    var requestedData = content.findById(extractLongFromCommand(messageText,PAID_COMMAND));
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
              data.getId(),
              data.getName(),
              data.getPrice(),
              user.getId(),
              operatorActionMessage);
      sendMessage(NOTIFICATION_CHANNEL_ID, channelMessageText);
    }
  }

  private void sendContentListMessage(Long chatId, String filter) {
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
                "/%s - %s - %s CZK\n", data.getId(), data.getName(), data.getPrice()));
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

    var paymentCommand = "/ZAPLACENO_" + selectedData.getId();

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
  // ADMIN

  private void sendMessage(Long chatId, String messageText) {
    if(messageText.isEmpty()){
      return;
    }
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
