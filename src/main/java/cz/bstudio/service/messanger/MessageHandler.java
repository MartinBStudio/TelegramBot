package cz.bstudio.service.messanger;

import static cz.bstudio.service.messanger.constants.Commands.*;

import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.content.Content;
import cz.bstudio.service.localization.Localization;
import java.util.LinkedList;
import java.util.List;

import cz.bstudio.service.messanger.model.BotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandler {
    protected final Content content;
    protected final Bot bot;
    protected final Localization localization;

    public LinkedList<BotResponse> handleCustomAdminMessages(
            String messageText, String username) {
        LinkedList<BotResponse> response = new LinkedList<>();
        switch (messageText) {
            case IS_ADMIN -> {
                response.add(BotResponse.builder().messageBody(String.valueOf(bot.isAdmin(username))).build());
                return response;
            }
            case DISPLAY_BOT_DETAILS -> {
                response.add(BotResponse.builder().messageBody(bot.display()).build());
                return response;
            }
            case CHANGE_LANGUAGE -> {
                localization.changeLocalization();
                response.add(BotResponse.builder().messageBody(String.format(localization.getLanguageChanged(), localization.getLocalization())).build());
                return response;
            }
        }
        if (isRemoveCommand(messageText)) {
            response.add(content.remove(getLongFromString(messageText)));
            return response;
        }
        if (isDisplayCommand(messageText)) {
            response.add(content.display(getLongFromString(messageText)));
            return response;
        }
        if (isEditCommand(messageText)) {
            response.add(content.edit(messageText));
            return response;
        }
        if (isUpdateBotDetailsCommand(messageText)) {
            response.add(bot.edit(messageText));
            return response;
        }
        if (isAddCommand(messageText)) {
            response.add(content.add(messageText));
            return response;
        }
        return response;
    }

    public LinkedList<BotResponse> handleCustomUserMessages(
            String messageText, User user) {
        LinkedList<BotResponse> response = new LinkedList<>();
        if (messageText.equals(START_COMMAND)) {
            response.add(content.getWelcomeResponse());
            return response;
        }
        var contentTypes = content.getContentTypes();
        for (String type : contentTypes) {
            if (messageText.equals("/" + type)) {
                response.add(content.getContentListResponse(type));
                return response;
            }
        }
        if (isNumberCommand(messageText)) {
            response.add(content.getSelectedContentResponse(messageText, user));
            return response;
        }
        if (isPaidCommand(messageText)) {
            response.addAll(content.getPaidResponses(messageText,user));
            return response;
        }
        return response;
    }
public List<BotResponse> getUnknownCommandResponse(){
var responses = new LinkedList<BotResponse>();
  responses.add(BotResponse.builder()
          .messageBody(localization.getInvalidRequest())
          .statusCode(405)
          .build());
  responses.add(content.getWelcomeResponse());
  return responses;

}


    public Long getNotificationChannel()
    {
        return bot.getNotificationChannel();
    }
}
