package cz.bstudio.service.localization;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import static cz.bstudio.constants.Constants.TELEGRAM_BOT_LOCALIZATION_ENV_VARIABLE;
import static cz.bstudio.constants.Constants.TELEGRAM_PROPERTIES_PATH;

@Service
@Getter
@PropertySource(TELEGRAM_PROPERTIES_PATH)
public class Localization {

  @Value(TELEGRAM_BOT_LOCALIZATION_ENV_VARIABLE)

  private String localization;

  public void changeLocalization() {
      if(localization.equals("CZE")){
          localization = "ENG";
      }
      else{
          localization = "CZE";
      }
  }

  private String getMessage(Messages message) {
    return localization.equals("CZE") ? message.getCzechMessage() : message.getEnglishMessage();
  }


  public String getWelcome() {
    return getMessage(Messages.WELCOME);
  }
  public String getLanguageChanged() {
    return getMessage(Messages.LANGUAGE_CHANGED);
  }
  public String getContentTypes() {
    return getMessage(Messages.CONTENT_TYPES);
  }

  public String getContentSelected() {
    return getMessage(Messages.CONTENT_SELECTED);
  }
  public String getNoAvailableContent() {
    return getMessage(Messages.NO_AVAILABLE_CONTENT);
  }

  public String getContentNotFound() {
    return getMessage(Messages.CONTENT_NOT_FOUND);
  }
  public String getContentNotAvailable() {
    return getMessage(Messages.CONTENT_NOT_AVAILABLE);
  }

  public String getThanks() {
    return getMessage(Messages.THANKS_MESSAGE);
  }

  public String getContactUser() {
    return getMessage(Messages.CONTACT_USER);
  }

  public String getUserWillContactYou() {
    return getMessage(Messages.USER_WILL_CONTACT_YOU);
  }

  public String getNoUsername() {
    return getMessage(Messages.NO_USERNAME_MESSAGE);
  }

  public String getPaymentGuide() {
    return getMessage(Messages.PAYMENT_GUIDE);
  }

  public String getInvalidRequest() {
    return getMessage(Messages.INVALID_REQUEST);
  }

  public String getContentDetails() {
    return getMessage(Messages.CONTENT_DETAILS);
  }

  public String getNotificationDetails() {
    return getMessage(Messages.NOTIFICATION_DETAILS);
  }
}
