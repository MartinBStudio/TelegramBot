package cz.bstudio.service.logger;

import static cz.bstudio.constants.Constants.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = LOG_TABLE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = LOG_SEQ)
  @SequenceGenerator(name = LOG_SEQ, sequenceName = LOG_SEQ, allocationSize = 1)
  private Long id;

  private String botName;
  private String username;
  private Long userId;
  private Long chatId;
  private Integer messageId;
  private String message;
  private Long responseTime;
  private String botResponse;
  private String userLanguage;
  private Boolean isGroupChat;
  private Boolean isAdmin;
  private Boolean isBot;
  private String errorMessage;
  private String telegramErrorMessage;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;
}
