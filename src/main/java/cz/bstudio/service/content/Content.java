package cz.bstudio.service.content;

import static cz.bstudio.service.utils.Utils.isNotEmpty;
import static cz.bstudio.service.messanger.Commands.*;

import cz.bstudio.service.bot.Bot;
import cz.bstudio.service.localization.Localization;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;
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

  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;

  public List<ContentEntity> getData(String filter) {
    return filter.isEmpty()
        ? contentRepository.findByOwner(BOT_USERNAME)
        : contentRepository.findByTypeAndOwner(filter, BOT_USERNAME);
  }
  public List<String> getContentTypes(){
    return contentRepository.findDistinctContentTypesByOwner(BOT_USERNAME);
  }
  public String buildContentTypesString() {
    var sb = new StringBuilder();
    var contentTypes = getContentTypes();
    for (String type : contentTypes) {
      sb.append("/" + type + "\n");
    }
    return sb.toString();
  }
  public String buildContentMessageFromStringIndex(String index, Long userId) {
    var botEntity = bot.getBotEntity();
    var selectedData = findOwnedById(Long.parseLong(index));
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
  public String buildSelectedContentMessage(String messageText, User user) {
    String message;
    var messageNumber = extractLongFromCommand(messageText, NUMBER_COMMAND);
    if (findOwnedById(messageNumber) != null) {
      message = buildContentMessageFromStringIndex(String.valueOf(messageNumber), user.getId());
    } else {
      message = localization.getContentOutOfBounds();
    }
    return message;
  }
  public String buildContentListMessage(String filter ) {
    String message = "";
    List<ContentEntity> content = getData(filter);
    if (!content.isEmpty()) {
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
                  String.format("/%s - %s - %s CZK\n", data.getId(), data.getName(), data.getPrice()));
        }
        message = sb.toString();
      }
    } else {
      message = "No Content found";
    }
    return message;
  }

  public ContentEntity findOwnedById(long id) {
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

  @Transactional
  public String remove(Long contentId) {
    Optional<ContentEntity> contentEntity = contentRepository.findById(contentId);
    if (contentEntity.isPresent()) {
      var entityToRemove = contentEntity.get();
      if (isOwner(entityToRemove.getOwner())) {
        contentRepository.delete(entityToRemove);
        return contentId + " - " + entityToRemove.getName() + " - REMOVED";
      } else {
        return "You are not owner of this content.";
      }

    } else {
      return "Content with id " + contentId + " not found.";
    }
  }

  @Transactional
  public String display(Long contentId) {
    Optional<ContentEntity> optionalEntity = contentRepository.findById(contentId);
    if (optionalEntity.isPresent()) {
      ContentEntity existingEntity = optionalEntity.get();
      if (isOwner(existingEntity.getOwner())) {
        return getReadableContentEntity(existingEntity);
      } else {
        return "Sorry, but you cannot display content you are not owner.";
      }
    } else {
      return "Content with id " + contentId + " not found.";
    }
  }

  @Transactional
  public String edit(String messageText) {
    try {
      var contentId = extractIndexFromEditMessage(messageText); // Extracts the ID from the command
      Optional<ContentEntity> contentEntityOptional = contentRepository.findById(contentId);
      if (contentEntityOptional.isPresent()) {
        var existingEntity = contentEntityOptional.get();
        if (isOwner(existingEntity.getOwner())) {
          var payload = extractPayloadFromEditRequest(messageText);
          ContentEntity newContentEntity = parsePayloadToContentEntity(payload);
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
          return "Sorry but you are not owner of" + contentId + "content.";
        }
        return "Content updated successfully:\n"+getReadableContentEntity(existingEntity);
      } else {
        return "Content with ID " + contentId + " not found.";
      }
    } catch (Exception e) {
      return "Editing of existing content failed, make sure you follow guide properly.";
    }
  }

  @Transactional
  public String add(String messageText) {
    try {
      var payload = extractAddPayload(messageText);
      ContentEntity newContentEntity = parsePayloadToContentEntity(payload);
      newContentEntity.setOwner(BOT_USERNAME);
      contentRepository.save(newContentEntity);
      return "New content added:\n" + getReadableContentEntity(newContentEntity);
    } catch (Exception e) {
      return "Adding of new content failed, make sure you follow guide properly.";
    }
  }

  private ContentEntity parsePayloadToContentEntity(String payload) {
    Map<String, String> fields =
        Arrays.stream(payload.replace("ContentEntity(", "").replace(")", "").split(", "))
            .map(s -> s.split("="))
            .collect(Collectors.toMap(a -> a[0].trim(), a -> a.length > 1 ? a[1].trim() : null));
    var parsedEntity =
        ContentEntity.builder()
            .name(fields.get("name"))
            .type(fields.get("type"))
            .subType(fields.get("subType"))
            .description(fields.get("description"))
            .previewUrl(fields.get("previewUrl"))
            .fullUrl(fields.get("fullUrl"))
            .build();
    var priceString = fields.get("price");
    if (fields.get("price") != null) {
      parsedEntity.setPrice(Integer.valueOf(priceString));
    }
    return parsedEntity;
  }
  private String getReadableContentEntity(ContentEntity contentEntity) {
    return String.format(
            "[ContentEntity(name=%s, type=%s, subType=%s, description=%s, price=%s, previewUrl=%s, fullUrl=%s)]",
            contentEntity.getName(),
            contentEntity.getType(),
            contentEntity.getSubType(),
            contentEntity.getDescription(),
            contentEntity.getPrice(),
            contentEntity.getPreviewUrl(),
            contentEntity.getFullUrl());
  }

  private boolean isOwner(String contentOwner) {
    return BOT_USERNAME.equals(contentOwner);
  }
}
