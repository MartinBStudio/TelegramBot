package cz.bstudio;

import static cz.bstudio.service.messanger.Commands.*;
import static org.testng.Assert.*;

import cz.bstudio.service.messanger.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class IntegrationTests extends AbstractTestNGSpringContextTests {

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
  @Test
  public void testSuccessAddDisplayAndRemove() {
    // Set up admin user
    User adminUser = createUser(adminUsername);
    message.setFrom(adminUser);

    // Step 1: Add a new entity
    message.setText("/ADD_[name=unitTest]");
    var response = telegramBot.handleIncomingRequest(update);
    var messageBody = response.getFirst().getMessageBody();

    // Extract the ID from the message body
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(messageBody);
    String id = "";
    if (matcher.find()) {
      id = matcher.group(); // Get the ID from the message body
    } else {
      fail("ID not found in the response message"); // Fail if no ID is found
    }

    // Assert that the addition was successful
    assertEquals(response.getFirst().getStatusCode(), 200, "Failed to add entity");

    // Step 2: Display the added entity
    message.setText("/DISPLAY_" + id);
    var displayResponse = telegramBot.handleIncomingRequest(update);

    // Assert that the display operation was successful
    assertEquals(displayResponse.getFirst().getStatusCode(), 200, "Failed to display entity");

    // Step 3: Remove the added entity
    message.setText("/REMOVE_" + id);
    var removeResponse = telegramBot.handleIncomingRequest(update);

    // Assert that the removal was successful
    assertEquals(removeResponse.getFirst().getStatusCode(), 200, "Failed to remove entity");
  }


}
