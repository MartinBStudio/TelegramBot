package cz.bstudio.service.messanger.model;

import cz.bstudio.service.messanger.constants.MessageChannels;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BotResponse {
  @Builder.Default private MessageChannels channel = MessageChannels.USER;
  private String messageBody;
  private Long chatId;
  @Builder.Default private boolean disableWebPreview = false;
  @Builder.Default private int statusCode =200;
}
