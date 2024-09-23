package cz.raadost.service.messanger;

import lombok.Getter;

@Getter
public class Commands {
  public static final String PAID_COMMAND = "/ZAPLACENO_\\d+";
  public static final String START_COMMAND = "/start";
  public static final String ALL_COMMAND = "/all";
  public static final String VIDEO_COMMAND = "/video";
  public static final String SPECIAL_COMMAND = "/special";
  public static final String BUNDLE_COMMAND = "/bundle";
  public static final String NUMBER_COMMAND = "/\\d+";
  //ADMIN
  public static final String REMOVE_CONTENT_COMMAND = "/REMOVE_\\d+";

  public static String removeSlash(String command) {
    return command.substring(1);
  }

  public static int getIntegerFromString(String string) {
    return Integer.parseInt(string.replaceAll("[^0-9]", ""));
  }
  public static Long getLongFromString(String string) {
    return Long.parseLong(string.replaceAll("[^0-9]", ""));
  }

  public static boolean isNumberCommand(String messageText) {
    return messageText.matches(NUMBER_COMMAND);
  }

  public static boolean isPaidCommand(String messageText) {
    return messageText.matches(PAID_COMMAND);
  }
  //ADMIN
  public static boolean isRemoveCommand(String messageText) {
    return messageText.matches(REMOVE_CONTENT_COMMAND);
  }
}
