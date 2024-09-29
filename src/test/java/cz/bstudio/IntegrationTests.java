package cz.bstudio;

import static cz.bstudio.service.messanger.Commands.*;
import static org.testng.Assert.*;

import cz.bstudio.service.messanger.TelegramBot;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@SpringBootTest
public class IntegrationTests extends AbstractTestNGSpringContextTests {

  private final String standardUsername = "asdasd";
  private final String adminUsername = "martin159951";

  @Autowired
  private TelegramBot telegramBot;

  private Update update;
  private Message message;

  @BeforeTest
  public void setUp() {
    Application.IS_DEBUG = true; // Consider using a test profile for debugging
    Chat chat = new Chat();
    chat.setId(100200300L);
    update = new Update();
    message = new Message();
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
    executeCommands(List.of(IS_ADMIN, CHANGE_LANGUAGE, DISPLAY_BOT_DETAILS, "/EDIT_BOT_[sellerName=unitChanged]"), adminUsername, 200);
    executeCommands(List.of(IS_ADMIN, CHANGE_LANGUAGE, DISPLAY_BOT_DETAILS, "/EDIT_BOT_[sellerName=unitChanged]"), standardUsername, 405);
  }

  private void executeCommands(List<String> commands, String username, int expectedStatus) {
    message.setFrom(createUser(username));
    for (String command : commands) {
      message.setText(command);
      var response = telegramBot.handleIncomingRequest(update);
      assertEquals(response.getFirst().getStatusCode(), expectedStatus, "Command: " + command);
    }
  }

  @Test
  public void testNoCommandMessage() {
    verifyNoCommandResponse(standardUsername);
    verifyNoCommandResponse(adminUsername);
  }

  private void verifyNoCommandResponse(String username) {
    message.setText("randomMessage");
    message.setFrom(createUser(username));
    var response = telegramBot.handleIncomingRequest(update);
    assertEquals(response.getFirst().getStatusCode(), 405, "Expected 405 for random message from " + username);
  }

  @Test
  public void testSuccessAddEditDisplayAndRemove() {
    User adminUser = createUser(adminUsername);
    message.setFrom(adminUser);

    String id = addEntity();
    assertEntityOperation("/EDIT_" + id + "_[name=unitTest]" );
    assertEntityOperation("/DISPLAY_" + id);
    assertEntityOperation("/REMOVE_" + id );
  }

  private String addEntity() {
    message.setText("/ADD_[name=unitTest]");
    var response = telegramBot.handleIncomingRequest(update);
    String messageBody = response.getFirst().getMessageBody();
    String id = extractId(messageBody);
    assertEquals(response.getFirst().getStatusCode(), 200, "Failed to add entity");
    return id;
  }

  private String extractId(String messageBody) {
    Matcher matcher = Pattern.compile("\\d+").matcher(messageBody);
    if (matcher.find()) {
      return matcher.group();
    } else {
      fail("ID not found in the response message");
      return ""; // This will never be reached, but required for compilation
    }
  }

  private void assertEntityOperation(String command ) {
    message.setText(command);
    var response = telegramBot.handleIncomingRequest(update);
    assertEquals(response.getFirst().getStatusCode(), 200, "Failed operation for command: " + command);
  }

  @Test
  public void testDisplayCommand() {
    assertCommandResponse("/DISPLAY_00", 404); // Not found
    assertCommandResponse("/DISPLAY_01", 401); // Not owned
    assertCommandResponse("/DISPLAY_asd", 405); // Wrong syntax
    assertCommandResponse("/DISPLAY_187", 200); // Success
  }

  @Test
  public void testEditBotCommand() {
    assertCommandResponse("/EDIT_BOT_[asd=asd]", 500); // Wrong fields
    assertCommandResponse("/EDIT_BOT_[asd]", 500); // Wrong fields
    assertCommandResponse("/EDIT_BOT_[paymentMethod1=unitTest]", 200); // Success single field
    assertCommandResponse("/EDIT_BOT_[sellerName=newName, paymentMethod1=unitTest]", 200); // Success multiple fields
  }

  @Test
  public void testEditContentCommand() {
    assertCommandResponse("/EDIT_187_[asd=asd]", 500); // Wrong fields
    assertCommandResponse("/EDIT_187_[asd]", 500); // Wrong fields
    assertCommandResponse("/EDIT_187_[name=unitTest]", 200); // Success single field
    assertCommandResponse("/EDIT_187_[name=newName, previewUrl=newUrl]", 200); // Success multiple fields
  }

  @Test
  public void testFailedAddCommand() {
    assertCommandResponse("/ADD_[asd=asd]", 500); // Wrong fields
    assertCommandResponse("/ADD_[asd]", 500); // Wrong fields
    assertCommandResponse("/ADD_[]", 500); // Success single field
  }

  private void assertCommandResponse(String displayCommand, int statusCode) {
    message.setFrom(createUser(adminUsername));
    message.setText(displayCommand);
    var responses = telegramBot.handleIncomingRequest(update);
    assertEquals(responses.getFirst().getStatusCode(), statusCode, "Expected status " + statusCode + " for command: " + displayCommand);
  }

  @Test
  public void testBasicUserCommands() {
    executeCommands(List.of(START_COMMAND, "/ZAPLACENO_187", "/187"), standardUsername, 200);
  }
}
