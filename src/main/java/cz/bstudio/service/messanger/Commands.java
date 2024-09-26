package cz.bstudio.service.messanger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class Commands {
  public static final String PAID_COMMAND = "/ZAPLACENO_(\\d+)";
  public static final String IS_ADMIN = "/IS_ADMIN";
  public static final String START_COMMAND = "/start";
  public static final String CHANGE_LANGUAGE = "/CHANGE_LANGUAGE";
  public static final String DISPLAY_BOT_DETAILS = "/DISPLAY_BOT";
  public static final String ALL_COMMAND = "/all";
  public static final String VIDEO_COMMAND = "/video";
  public static final String SPECIAL_COMMAND = "/special";
  public static final String BUNDLE_COMMAND = "/bundle";
  public static final String PHOTOS_COMMAND = "/photos";
  public static final String NUMBER_COMMAND = "/(\\d+)";
  // ADMIN
  public static final String REMOVE_CONTENT_COMMAND = "/REMOVE_(\\d+)";
  public static final String DISPLAY_CONTENT_COMMAND = "/DISPLAY_(\\d+)";
  public static final String EDIT_CONTENT_COMMAND = "/EDIT_(\\d+)_\\[(.*?)\\]";
  public static final String EDIT_BOT_COMMAND = "/EDIT_BOT_\\[(.*?)\\]";

  public static final String ADD_CONTENT_COMMAND = "/ADD_\\[.*\\]";

  public static Long getLongFromString(String string) {
    return Long.parseLong(string.replaceAll("[^0-9]", ""));
  }

  public static Long extractLongFromCommand(String input, String command) {
    Pattern pattern = Pattern.compile(command);
    Matcher matcher = pattern.matcher(input);

    if (matcher.find()) {
      return Long.parseLong(matcher.group(1)); // Group 1 is the number after REMOVE_
    }
    throw new IllegalArgumentException("Invalid command format, number not found.");
  }

  public static boolean isNumberCommand(String messageText) {
    return messageText.matches(NUMBER_COMMAND);
  }

  public static boolean isPaidCommand(String messageText) {
    return messageText.matches(PAID_COMMAND);
  }

  // ADMIN
  public static boolean isRemoveCommand(String messageText) {
    return messageText.matches(REMOVE_CONTENT_COMMAND);
  }

  public static boolean isDisplayCommand(String messageText) {
    return messageText.matches(DISPLAY_CONTENT_COMMAND);
  }

  public static boolean isEditCommand(String messageText) {
    return messageText.matches(EDIT_CONTENT_COMMAND);
  }
  public static boolean isUpdateBotDetailsCommand(String messageText) {
    return messageText.matches(EDIT_BOT_COMMAND);
  }

  public static boolean isAddCommand(String messageText) {
    return messageText.matches(ADD_CONTENT_COMMAND);
  }

  public static String extractAddPayload(String message) {
    // Match the number after /ADD_ and the payload inside the square brackets
    Pattern pattern = Pattern.compile("/ADD_\\[(.*)\\]");
    Matcher matcher = pattern.matcher(message);
    if (matcher.find()) {
      String payload = matcher.group(1); // Group 2 is the payload
      return payload;
    } else {
      throw new IllegalArgumentException("Invalid command format.");
    }
  }

  public static String extractPayloadFromEditRequest(String command) {
    // Regular expression to match the command format
    String regex = EDIT_CONTENT_COMMAND;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);

    // Check if the command matches the regex
    if (matcher.find()) {
      // Extract index and payload
      return matcher.group(2);
    }
    return null;
  }
  public static String extractPayloadFromEditBotRequest(String command) {
    // Regular expression to match the command format
    String regex = EDIT_BOT_COMMAND;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);

    // Check if the command matches the regex
    if (matcher.find()) {
      // Extract index and payload
      return matcher.group(1);
    }
    return null;
  }

  public static Long extractIndexFromEditMessage(String command) {
    // Regular expression to match the command format
    String regex = EDIT_CONTENT_COMMAND;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);
    // Check if the command matches the regex
    if (matcher.find()) {
      // Extract index and payload
      return Long.parseLong(matcher.group(1));
    }
    return null;
  }
}
