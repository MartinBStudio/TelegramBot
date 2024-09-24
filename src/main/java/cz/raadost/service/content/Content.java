package cz.raadost.service.content;


import static cz.raadost.service.messanger.Commands.*;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  public String edit(Long contentId) {
    Optional<ContentEntity> contentEntity = contentRepository.findById(contentId);
    if (contentEntity.isPresent()) {
      return "Editing" + contentId;
    } else {
      return "Content with id " + contentId + " not found.";
    }
  }

  @Transactional
  public String add(String messageText) {
    try{
      var payload = extractPayload(messageText);
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
