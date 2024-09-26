package cz.bstudio.service.content;

import static cz.bstudio.constants.DbObjects.CONTENT_SEQ;
import static cz.bstudio.constants.DbObjects.CONTENT_TABLE;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = CONTENT_TABLE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = CONTENT_SEQ)
  @SequenceGenerator(name = CONTENT_SEQ, sequenceName = CONTENT_SEQ, allocationSize = 1)
  private Long id;

  private String name;
  private String type;
  private String owner;
  private String subType;
  private String description;
  private Integer price;
  private String previewUrl;
  private String fullUrl;
}
