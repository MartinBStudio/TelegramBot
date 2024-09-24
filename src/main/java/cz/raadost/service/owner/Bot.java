package cz.raadost.service.owner;


import static cz.raadost.service.messanger.Commands.*;

import cz.raadost.service.content.ContentEntity;
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
public class Bot {
  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;
  private final BotRepository contentRepository;


  public BotEntity getBotEntity() {
    Optional<BotEntity> optionalContent = contentRepository.findBotEntityByBotName(BOT_USERNAME);
    return optionalContent.orElse(null);
  }
  public String display() {
    Optional<BotEntity> optionalContent = contentRepository.findBotEntityByBotName(BOT_USERNAME);
    return optionalContent.toString();
  }
  @Transactional
  public String edit(String messageText) {
    try {
      Optional<BotEntity> optionalBotEntity = contentRepository.findBotEntityByBotName(BOT_USERNAME);
      if (optionalBotEntity.isPresent()) {
        var existingEntity = optionalBotEntity.get();
        var payload = extractPayloadFromEditBotRequest(messageText);
        BotEntity newBotEntity = parsePayloadToContentEntity(payload);
        existingEntity.setPaymentMethod1(newBotEntity.getPaymentMethod1());
        existingEntity.setPaymentMethod2(newBotEntity.getPaymentMethod2());
        contentRepository.save(existingEntity);
        return "Editing bot properties was successful.";
      } else {
        return "Bot does not exist.";
      }
    } catch (Exception e) {
      return "Editing of bot details failed, make sure you follow guide properly." + e.getMessage();
    }
  }
  private BotEntity parsePayloadToContentEntity(String payload) {
    Map<String, String> fields =
            Arrays.stream(payload.replace("BotEntity(", "").replace(")", "").split(", "))
                    .map(s -> s.split("="))
                    .collect(Collectors.toMap(a -> a[0].trim(), a -> a.length > 1 ? a[1].trim() : null));

    return BotEntity.builder()
            .botName(fields.get("botName"))
            .botToken(fields.get("botToken"))
            .sellerName(fields.get("sellerName"))
            .paymentMethod1(fields.get("paymentMethod1"))
            .paymentMethod2(fields.get("paymentMethod2"))
            .notificationChannel(fields.get("notificationChannel"))
            .build();
  }
}
