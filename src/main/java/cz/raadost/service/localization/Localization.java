package cz.raadost.service.localization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Localization {

    @Value("${telegram.bot.localization}")
    private String localization;

    private String getMessage(StaticMessages message) {
        return localization.equals("czech") ? message.getCzechMessage() : message.getEnglishMessage();
    }

    public String getWelcome() {
        return getMessage(StaticMessages.WELCOME);
    }

    public String getContentSelected() {
        return getMessage(StaticMessages.CONTENT_SELECTED);
    }

    public String getContentOutOfBounds() {
        return getMessage(StaticMessages.CONTENT_OUT_OF_BOUNDS);
    }

    public String getThanks() {
        return getMessage(StaticMessages.THANKS_MESSAGE);
    }

    public String getContactUser() {
        return getMessage(StaticMessages.CONTACT_USER);
    }

    public String getUserWillContactYou() {
        return getMessage(StaticMessages.USER_WILL_CONTACT_YOU);
    }

    public String getNoUsername() {
        return getMessage(StaticMessages.NO_USERNAME_MESSAGE);
    }

    public String getPaymentGuide() {
        return getMessage(StaticMessages.PAYMENT_GUIDE);
    }

    public String getPickContent() {
        return getMessage(StaticMessages.PICK_CONTENT);
    }

    public String getInvalidRequest() {
        return getMessage(StaticMessages.INVALID_REQUEST);
    }
}
