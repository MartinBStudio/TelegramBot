package cz.bstudio.service.bot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static cz.bstudio.constants.DbObjects.*;

@Entity
@Table(name = BOTS_TABLE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BotEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = BOT_SEQ)
  @SequenceGenerator(name = BOT_SEQ, sequenceName = BOT_SEQ, allocationSize = 1)
  private Long id;

  private String botName;
  private String botToken;
  private String sellerName;
  private String paymentMethod1;
  private String paymentMethod2;
  private String notificationChannel;
  @Convert(converter = StringListConverter.class)
  @Builder.Default private List<String> adminUsers = new ArrayList<>();

}
