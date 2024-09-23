package cz.raadost.dataSource;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

  private final ContentRepository contentRepository;

  public List<ContentEntity> findAll() {
    return contentRepository.findAll();
  }
  public List<ContentEntity> getData(String filter) {
    return filter.isEmpty()?contentRepository.findAll():contentRepository.findByType(filter);
  }
  public ContentEntity findById(long id) {
    Optional<ContentEntity> optionalContent = contentRepository.findById(id);
    return optionalContent.orElse(null); // or handle it in a way you prefer
  }
  public ContentEntity save(ContentEntity contentEntity) {
    return contentRepository.save(contentEntity);
  }
}
