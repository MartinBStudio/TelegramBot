package cz.bstudio.service.content;

import static cz.bstudio.constants.Constants.TELEGRAM_BOT_USERNAME_ENV_VARIABLE;
import static cz.bstudio.service.messanger.constants.Commands.*;
import static cz.bstudio.service.messanger.constants.MessageChannels.NOTIFICATION;
import static cz.bstudio.service.utils.Utils.*;

import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.localization.Localization;
import cz.bstudio.service.messanger.model.BotResponse;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class Content {
  private final ContentRepository contentRepository;
  private final Bot bot;
  private final Localization localization;
  private static final String CONTENT_ENTITY_FIELD = "ContentEntity";
  private static final String NAME_FIELD = "name";
  private static final String TYPE_FIELD = "type";
  private static final String SUB_TYPE_FIELD = "subType";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String PRICE_FIELD = "price";
  private static final String PREVIEW_URL_FIELD = "previewUrl";
  private static final String FULL_URL_FIELD = "fullUrl";
  private static final List<String> PAYLOAD_FIELDS = List.of(NAME_FIELD, TYPE_FIELD, SUB_TYPE_FIELD,DESCRIPTION_FIELD,PRICE_FIELD,PREVIEW_URL_FIELD,FULL_URL_FIELD);
  @Value(TELEGRAM_BOT_USERNAME_ENV_VARIABLE)
  private String BOT_USERNAME;




  @Transactional
  public BotResponse remove(Long contentId) {
    Optional<ContentEntity> contentEntity = contentRepository.findById(contentId);
    var botResponse = BotResponse.builder().build();
    if (contentEntity.isPresent()) {
      var entityToRemove = contentEntity.get();
      if (isOwner(entityToRemove.getOwner())) {
        contentRepository.delete(entityToRemove);
        botResponse.setMessageBody(contentId + " - " + entityToRemove.getName() + " - REMOVED");
      } else {
        botResponse.setMessageBody(localization.getContentNotAvailable());
        botResponse.setStatusCode(401);
      }

    } else {
      botResponse.setMessageBody(localization.getContentNotFound());
      botResponse.setStatusCode(404);
    }
    return botResponse;
  }
  @Transactional
  public BotResponse display(Long contentId) {
    Optional<ContentEntity> optionalEntity = contentRepository.findById(contentId);
    var botResponse = BotResponse.builder().build();
    if (optionalEntity.isPresent()) {
      ContentEntity existingEntity = optionalEntity.get();
      if (isOwner(existingEntity.getOwner())) {
        botResponse.setMessageBody(getReadableContentEntity(existingEntity));
        return botResponse;
      } else {
        botResponse.setMessageBody(String.format(localization.getContentNotAvailable(),contentId));
        botResponse.setStatusCode(401);
        return botResponse;
      }
    } else {
      botResponse.setMessageBody(String.format(localization.getContentNotFound(),contentId));
      botResponse.setStatusCode(404);
      return botResponse;
    }
  }
  @Transactional
  public BotResponse edit(String messageText) {
    var botResponse = BotResponse.builder().build();
    try {
      var contentId = extractIndexFromEditMessage(messageText); // Extracts the ID from the command
      Optional<ContentEntity> contentEntityOptional = contentRepository.findById(contentId);
      if (contentEntityOptional.isPresent()) {
        var existingEntity = contentEntityOptional.get();
        if (isOwner(existingEntity.getOwner())) {
          var payload = extractPayloadFromEditRequest(messageText);
          ContentEntity newContentEntity = getContentEntityFromFields(payload);
          if (isNotEmpty(newContentEntity.getName())) {
            existingEntity.setName(newContentEntity.getName());
          }
          if (isNotEmpty(newContentEntity.getType())) {
            existingEntity.setType(newContentEntity.getType());
          }
          if (isNotEmpty(newContentEntity.getDescription())) {
            existingEntity.setDescription(newContentEntity.getDescription());
          }
          if (newContentEntity.getPrice() != null) {
            existingEntity.setPrice(newContentEntity.getPrice());
          }
          if (isNotEmpty(newContentEntity.getPreviewUrl())) {
            existingEntity.setPreviewUrl(newContentEntity.getPreviewUrl());
          }
          if (isNotEmpty(newContentEntity.getSubType())) {
            existingEntity.setSubType(newContentEntity.getSubType());
          }
          if (isNotEmpty(newContentEntity.getFullUrl())) {
            existingEntity.setFullUrl(newContentEntity.getFullUrl());
          }
          contentRepository.save(existingEntity);
        } else {
          botResponse.setMessageBody(localization.getContentNotAvailable());
          botResponse.setStatusCode(401);
          return botResponse;
        }
        botResponse.setMessageBody("Content updated successfully:\n"+getReadableContentEntity(existingEntity));
       return botResponse;
      } else {
        botResponse.setMessageBody(localization.getContentNotFound());
        botResponse.setStatusCode(404);
        return botResponse;
      }
    } catch (Exception e) {
      botResponse.setMessageBody("Editing of existing content failed, make sure you follow guide properly.\n\n" + e.getMessage());
      botResponse.setStatusCode(500);
      return botResponse;
    }
  }
  @Transactional
  public BotResponse add(String messageText) {
    var botResponse = BotResponse.builder().build();
    try {
      var payload = extractAddPayload(messageText);
      ContentEntity newContentEntity = getContentEntityFromFields(payload);
      newContentEntity.setOwner(BOT_USERNAME);
      var addedContent =contentRepository.save(newContentEntity);
      botResponse.setMessageBody(String.format("New content added: ID %s\n %s.",addedContent.getId(),getReadableContentEntity(newContentEntity)));
      return botResponse;
    } catch (Exception e) {
      botResponse.setMessageBody("Adding of new content failed, make sure you follow guide properly." + e.getMessage());
      botResponse.setStatusCode(500);
      return botResponse;
    }
  }
  public List<String> getContentTypes(){
    return contentRepository.findDistinctContentTypesByOwner(BOT_USERNAME);
  }
  public BotResponse getWelcomeResponse(){
   var contentSize = getData("").size();
   var welcome = String.format(localization.getWelcome());
   var message = contentSize > 0
           ? welcome+localization.getContentTypes() + buildContentTypesString()
           : welcome+localization.getNoAvailableContent();
   return BotResponse.builder().messageBody(message).build();
 }
  public BotResponse getSelectedContentResponse(String messageText, User user) {
    var botResponse = BotResponse.builder().build();
    var messageNumber = extractLongFromCommand(messageText, NUMBER_COMMAND);
      var selectedContent =findOwnedById(messageNumber);
      if(selectedContent!=null){
      botResponse.setMessageBody(buildContentMessageFromStringIndex(user.getId(),selectedContent));
      }
      else{
        botResponse.setMessageBody(localization.getContentNotAvailable());
        botResponse.setStatusCode(401);
      }
      return botResponse;
  }
  public BotResponse getContentListResponse(String filter ) {
    var botResponse = BotResponse.builder().build();
    StringBuilder sb = new StringBuilder();
    List<ContentEntity> content = getData(filter);
    if (!content.isEmpty()) {
        for (ContentEntity data : content) {
          sb.append(
                  String.format("/%s - %s - %s CZK\n", data.getId(), data.getName(), data.getPrice()));
        }
    } else {
      botResponse.setMessageBody(localization.getContentNotFound());
      botResponse.setStatusCode(404);
      return botResponse;
    }
    botResponse.setMessageBody(sb.toString());
    return botResponse;
  }
  public List<BotResponse> getPaidResponses(String messageText,User user){
    var requestedData = findOwnedById(extractLongFromCommand(messageText, PAID_COMMAND));
    var responses = new ArrayList<BotResponse>();
    if (requestedData != null) {
      String message =
              (user.getUserName() == null)
                      ? String.format(
                      localization.getNoUsername(), bot.getBotEntity().getAdminUsers().get(0))
                      : String.format(localization.getThanks(), bot.getBotEntity().getSellerName());
      var notificationMessage=buildNotificationMessage(requestedData, user);
      responses.add(BotResponse.builder().messageBody(message).build());
      responses.add(BotResponse.builder().channel(NOTIFICATION).messageBody(notificationMessage).build());
    }
    else{
      responses.add(BotResponse.builder().messageBody(localization.getContentNotFound()).statusCode(404).build());
    }
      return responses;
  }
  private List<ContentEntity> getData(String filter) {
    return filter.isEmpty()
            ? contentRepository.findByOwner(BOT_USERNAME)
            : contentRepository.findByTypeAndOwner(filter, BOT_USERNAME);
  }
  private ContentEntity findOwnedById(long id) {
    Optional<ContentEntity> optionalContent = contentRepository.findById(id);
    if(optionalContent.isPresent()){
      var foundContent = optionalContent.get();
      if (isOwner(foundContent.getOwner())) {
        return foundContent;
      } else {
        return null;
      }
    }
    else{
      return null;
    }
  }
  private String buildNotificationMessage(ContentEntity data, User user) {
    String username = user.getUserName();
    String message;
    String usernameDisplay = (username == null) ? "n/a" : "@" + username;
    var operatorActionMessage =
            (username == null) ? localization.getUserWillContactYou() : localization.getContactUser();
    message =
            String.format(
                    localization.getNotificationDetails(),
                    usernameDisplay,
                    data.getId(),
                    data.getName(),
                    data.getPrice(),
                    user.getId(),
                    operatorActionMessage);
    return message;
  }
  private String buildContentTypesString() {
    var sb = new StringBuilder();
    var contentTypes = getContentTypes();
    for (String type : contentTypes) {
      sb.append("/").append(type).append("\n");
    }
    return sb.toString();
  }
  private String buildContentMessageFromStringIndex(Long userId,ContentEntity selectedData) {
    var botEntity = bot.getBotEntity();
    var contentSelected = localization.getContentSelected();
    var contentName = selectedData.getName();
    var contentType = selectedData.getType();
    var contentDescription = selectedData.getDescription();
    var contentPrice = selectedData.getPrice();
    var payment1 = botEntity.getPaymentMethod1();
    var payment2 = botEntity.getPaymentMethod2();
    var paymentGuide = localization.getPaymentGuide();

    var paymentCommand = "/ZAPLACENO_" + selectedData.getId();

    return String.format(
            localization.getContentDetails(),
            contentSelected,
            contentName,
            contentType,
            contentDescription,
            contentPrice,
            payment1,
            payment2,
            userId,
            paymentGuide,
            paymentCommand);
  }
  private ContentEntity getContentEntityFromFields(String payload) {
    var fields = parsePayload(payload, CONTENT_ENTITY_FIELD, PAYLOAD_FIELDS);
    // Check if at least one of the required fields is present directly from the map
    checkLeastOneField(fields);
    // Return the ContentEntity using the map values directly
    String type = fields.get(TYPE_FIELD);
    if(type==null){
      type="Default";
    }
    var contentEntity = ContentEntity.builder()
            .name(fields.get(NAME_FIELD))
            .type(type)
            .subType(fields.get(SUB_TYPE_FIELD))
            .description(fields.get(DESCRIPTION_FIELD))
            .previewUrl(fields.get(PREVIEW_URL_FIELD))
            .fullUrl(fields.get(FULL_URL_FIELD))
            .build();
    if (fields.get(PRICE_FIELD) != null) {
        contentEntity.setPrice(Integer.parseInt(fields.get(PRICE_FIELD)));
    }
   return contentEntity;
  }
  private String getReadableContentEntity(ContentEntity contentEntity) {
    return String.format(
            "[%s(%s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s)]",
            CONTENT_ENTITY_FIELD,
            NAME_FIELD,
            contentEntity.getName(),
            TYPE_FIELD,
            contentEntity.getType(),
            SUB_TYPE_FIELD,
            contentEntity.getSubType(),
            DESCRIPTION_FIELD,
            contentEntity.getDescription(),
            PRICE_FIELD,
            contentEntity.getPrice(),
            PREVIEW_URL_FIELD,
            contentEntity.getPreviewUrl(),
            FULL_URL_FIELD,
            contentEntity.getFullUrl());
  }
  private boolean isOwner(String contentOwner) {
    return BOT_USERNAME.equals(contentOwner);
  }
}
