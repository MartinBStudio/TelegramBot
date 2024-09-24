package cz.raadost.service.content;


import static cz.raadost.service.messanger.Commands.*;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class Content {

  private final ContentRepository contentRepository;

  public List<ContentEntity> findAll() {
    return contentRepository.findAll();
  }

  public List<ContentEntity> getData(String filter) {
    return filter.isEmpty() ? contentRepository.findAll() : contentRepository.findByType(filter);
  }

  public ContentEntity findById(long id) {
    Optional<ContentEntity> optionalContent = contentRepository.findById(id);
    return optionalContent.orElse(null); // or handle it in a way you prefer
  }

  public ContentEntity save(ContentEntity contentEntity) {
    return contentRepository.save(contentEntity);
  }

  @Transactional
  public String remove(Long contentId) {
    Optional<ContentEntity> contentEntity = contentRepository.findById(contentId);
    if (contentEntity.isPresent()) {
      var entityToRemove = contentEntity.get();
      contentRepository.delete(entityToRemove);
      return contentId + " - " + entityToRemove.getName() + " - REMOVED";
    } else {
      return "Content with id " + contentId + " not found.";
    }
  }

  @Transactional
  public String display(Long contentId) {
    Optional<ContentEntity> contentEntity = contentRepository.findById(contentId);
    if (contentEntity.isPresent()) {
      return contentEntity.toString();
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
        var payload = extractPayloadFromEditRequest(messageText);
        ContentEntity newContentEntity = parsePayloadToContentEntity(payload);
        existingEntity.setName(newContentEntity.getName());
        existingEntity.setType(newContentEntity.getType());
        existingEntity.setDescription(newContentEntity.getDescription());
        existingEntity.setPrice(newContentEntity.getPrice());
        existingEntity.setPreviewUrl(newContentEntity.getPreviewUrl());
        existingEntity.setSubType(newContentEntity.getSubType());
        existingEntity.setFullUrl(newContentEntity.getFullUrl());
        contentRepository.save(existingEntity);
        return "Editing content " + contentId + " was successful.";
      } else {
        return "Content with ID " + contentId + " not found.";
      }
    } catch (Exception e) {
      return "Editing of existing content failed, make sure you follow guide properly.";
    }
  }


  @Transactional
  public String add(String messageText) {
    try{
      var payload = extractAddPayload(messageText);
      ContentEntity newContentEntity = parsePayloadToContentEntity(payload);
      contentRepository.save(newContentEntity);
      return "New content added - " + newContentEntity;
    }
    catch (Exception e) {
      return "Adding of new content failed, make sure you follow guide properly.";
    }
  }

  private ContentEntity parsePayloadToContentEntity(String payload) {
    Map<String, String> fields =
        Arrays.stream(payload.replace("ContentEntity(", "").replace(")", "").split(", "))
            .map(s -> s.split("="))
            .collect(Collectors.toMap(a -> a[0].trim(), a -> a.length > 1 ? a[1].trim() : null));

    return ContentEntity.builder()
        .name(fields.get("name"))
        .type(fields.get("type"))
        .subType(fields.get("subType"))
        .description(fields.get("description"))
        .price(Integer.parseInt(fields.get("price")))
        .previewUrl(fields.get("previewUrl"))
        .fullUrl(fields.get("fullUrl"))
        .build();
  }
}
