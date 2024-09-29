package cz.bstudio;

import static cz.bstudio.service.messanger.Commands.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.bot.BotEntity;
import cz.bstudio.service.logger.Logger;
import cz.bstudio.service.logger.LogEntity;
import cz.bstudio.service.messanger.BotResponse;
import cz.bstudio.service.messanger.MessageHandler;
import cz.bstudio.service.messanger.TelegramBot;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class UnitTests extends AbstractTestNGSpringContextTests {

  @Autowired
  private TelegramBot telegramBot;

  private Update update;
  private Message message;

private final String standardUsername = "asdasd";
private final String adminUsername = "martin159951";
  @BeforeMethod
  public void setUp() {
    Application.IS_DEBUG = true; // Consider using a test profile for debugging
    update = new Update();
    message = new Message();
    var chat = new Chat();
    chat.setId(100200300L);
    update.setMessage(message);
    message.setChat(chat);
  }

  private User createUser(String username) {
    User user = new User();
    user.setUserName(username);
    return user;
  }

  @Test
  public void testBasicAdminCommands() {
    executeBasicAdminCommands (adminUsername,200);
    executeBasicAdminCommands (standardUsername,405);
  }
  private void executeBasicAdminCommands (String username,int expected) {
    var basicAdminCommands = List.of(IS_ADMIN,CHANGE_LANGUAGE,DISPLAY_BOT_DETAILS);
    message.setFrom(createUser(username));
    for(String basicCommand: basicAdminCommands) {
      message.setText(basicCommand);
      var response = telegramBot.handleIncomingRequest(update);
      assertEquals(response.getFirst().getStatusCode(), expected);
    }
  }
  @Test
  public void testNoCommandMessage() {
    verifyNoCommandResponse(standardUsername);
    verifyNoCommandResponse(adminUsername);
  }
  private void verifyNoCommandResponse(String username) {
    message.setText("randomMessage");
    message.setFrom(createUser(username)); // Create standard user
    var response = telegramBot.handleIncomingRequest(update);
    assertEquals(response.getFirst().getStatusCode(), 405);
  }
}
