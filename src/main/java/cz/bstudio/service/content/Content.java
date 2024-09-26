package cz.bstudio.service.content;

import static cz.bstudio.service.Utils.isNotEmpty;
import static cz.bstudio.service.messanger.Commands.*;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Content {
  private final ContentRepository contentRepository;

  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;

  public List<ContentEntity> getData(String filter) {
    return filter.isEmpty()
        ? contentRepository.findByOwner(BOT_USERNAME)
        : contentRepository.findByTypeAndOwner(filter, BOT_USERNAME);
  }

  public ContentEntity findById(long id) {
    Optional<ContentEntity> optionalContent = contentRepository.findById(id);
    if (isOwner(optionalContent.get().getOwner())) {
      return optionalContent.get();
    } else {
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

  private boolean isOwner(String contentOwner) {
    return BOT_USERNAME.equals(contentOwner);
  }
}
