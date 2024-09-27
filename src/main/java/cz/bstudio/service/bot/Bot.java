package cz.bstudio.service.bot;


import static cz.bstudio.service.utils.Utils.isNotEmpty;
import static cz.bstudio.service.messanger.Commands.*;

import jakarta.transaction.Transactional;
import java.util.Arrays;
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
  private final BotRepository contentRepository;
  @Value("${telegram.bot.username}")
  private String BOT_USERNAME;

  public BotEntity getBotEntity() {
    Optional<BotEntity> optionalContent = contentRepository.findBotEntityByBotName(BOT_USERNAME);
    return optionalContent.orElse(null);
  }
  public String display() {
    Optional<BotEntity> optionalContent = contentRepository.findBotEntityByBotName(BOT_USERNAME);
    if (optionalContent.isPresent()) {
      BotEntity botEntity = optionalContent.get();
      // Create a custom formatted string with only the desired fields
      return getReadableBotEntity(botEntity);
    }
    return "Bot not found";
  }
  @Transactional
  public String edit(String messageText) {
    try {
      Optional<BotEntity> optionalBotEntity = contentRepository.findBotEntityByBotName(BOT_USERNAME);
      if (optionalBotEntity.isPresent()) {
        var existingEntity = optionalBotEntity.get();
        if(existingEntity.getBotName().equals(BOT_USERNAME)) {
          var payload = extractPayloadFromEditBotRequest(messageText);
          BotEntity newBotEntity = parsePayloadToContentEntity(payload);
          var newPaymentMethod1 = newBotEntity.getPaymentMethod1();
          var newPaymentMethod2 = newBotEntity.getPaymentMethod2();
          var newSellerName = newBotEntity.getSellerName();
          if(isNotEmpty(newPaymentMethod1)){
            existingEntity.setPaymentMethod1(newPaymentMethod1);
          }
          if(isNotEmpty(newPaymentMethod2)){
            existingEntity.setPaymentMethod2(newPaymentMethod2);
          }
          if(isNotEmpty(newSellerName)){
            existingEntity.setSellerName(newSellerName);
          }
          contentRepository.save(existingEntity);
          return "Bot details updated successfully:\n"+getReadableBotEntity(existingEntity);
        }
        else{
          return "Not enough permissions.";
        }
      } else {
        return "Bot does not exist.";
      }
    } catch (Exception e) {
      return "Editing of bot details failed, make sure you follow guide properly.";
    }
  }
  public boolean isAdmin(String userName) {
    return getBotEntity().getAdminUsers().contains(userName);
  }

  private String getReadableBotEntity(BotEntity botEntity) {
    return String.format("[BotEntity(sellerName=%s, paymentMethod1=%s, paymentMethod2=%s)]",
            botEntity.getSellerName(),
            botEntity.getPaymentMethod1(),
            botEntity.getPaymentMethod2());
  }
  private BotEntity parsePayloadToContentEntity(String payload) {
    Map<String, String> fields =
            Arrays.stream(payload.replace("BotEntity(", "").replace(")", "").split(", "))
                    .map(s -> s.split("="))
                    .collect(Collectors.toMap(a -> a[0].trim(), a -> a.length > 1 ? a[1].trim() : null));
 var sellerName = fields.get("sellerName");
 var paymentMethod1 = fields.get("paymentMethod1");
 var paymentMethod2 = fields.get("paymentMethod2");
    return BotEntity.builder()
            .sellerName(sellerName)
            .paymentMethod1(paymentMethod1)
            .paymentMethod2(paymentMethod2)
            .build();
  }
}
