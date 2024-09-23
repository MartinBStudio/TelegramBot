package cz.raadost.service.content;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
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
      contentRepository.deleteByContentIndex(contentId);
      return contentId + " - " + contentEntity.get().getName() + " - REMOVED";
    } else {
      return "Content with id " + contentId + " not found.";
    }
  }
}
