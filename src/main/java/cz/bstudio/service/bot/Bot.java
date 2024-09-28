package cz.bstudio.service.bot;

import static cz.bstudio.constants.Constants.TELEGRAM_BOT_USERNAME_ENV_VARIABLE;
import static cz.bstudio.constants.Constants.TELEGRAM_PROPERTIES_PATH;
import static cz.bstudio.service.messanger.Commands.*;
import static cz.bstudio.service.utils.Utils.*;

import cz.bstudio.exception.BotNotFoundException;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource(TELEGRAM_PROPERTIES_PATH)
public class Bot {

  final String BOT_ENTITY_FIELD = "BotEntity";
  final String PAYMENT_METHOD1_FIELD = "paymentMethod1";
  final String PAYMENT_METHOD2_FIELD = "paymentMethod2";
  final String SELLER_NAME_FIELD = "sellerName";
  final List<String> PAYLOAD_FIELDS = List.of(SELLER_NAME_FIELD, PAYMENT_METHOD1_FIELD, PAYMENT_METHOD2_FIELD);
  private final BotRepository contentRepository;

  @Value(TELEGRAM_BOT_USERNAME_ENV_VARIABLE)
  private String BOT_USERNAME;


  public BotEntity getBotEntity() {
    return contentRepository
        .findBotEntityByBotName(BOT_USERNAME)
        .orElseThrow(
            () ->
                new BotNotFoundException(
                    String.format(
                        "Make sure bot with name %s is added to your DB.", BOT_USERNAME)));
  }

  @Transactional
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
      Optional<BotEntity> optionalBotEntity =
          contentRepository.findBotEntityByBotName(BOT_USERNAME);
      if (optionalBotEntity.isPresent()) {
        var existingEntity = optionalBotEntity.get();
        if (existingEntity.getBotName().equals(BOT_USERNAME)) {
          var payload = extractPayloadFromEditBotRequest(messageText);
          BotEntity newBotEntity = getBotEntityFromPayload(payload);
          var newPaymentMethod1 = newBotEntity.getPaymentMethod1();
          var newPaymentMethod2 = newBotEntity.getPaymentMethod2();
          var newSellerName = newBotEntity.getSellerName();
          if (isNotEmpty(newPaymentMethod1)) {
            existingEntity.setPaymentMethod1(newPaymentMethod1);
          }
          if (isNotEmpty(newPaymentMethod2)) {
            existingEntity.setPaymentMethod2(newPaymentMethod2);
          }
          if (isNotEmpty(newSellerName)) {
            existingEntity.setSellerName(newSellerName);
          }
          contentRepository.save(existingEntity);
          return "Bot details updated successfully:\n" + getReadableBotEntity(existingEntity);
        } else {
          return "Not enough permissions.";
        }
      } else {
        return "Bot does not exist.";
      }
    } catch (Exception e) {
      return "Editing of bot details failed, make sure you follow guide properly.\n\n"
          + e.getMessage();
    }
  }

  public boolean isAdmin(String userName) {
    return getBotEntity().getAdminUsers().contains(userName);
  }

  private String getReadableBotEntity(BotEntity botEntity) {
    return String.format(
        "[(%s=%s, %s=%s, %s=%s)]",
        SELLER_NAME_FIELD,
        botEntity.getSellerName(),
        PAYMENT_METHOD1_FIELD,
        botEntity.getPaymentMethod1(),
        PAYMENT_METHOD2_FIELD,
        botEntity.getPaymentMethod2());
  }

  private BotEntity getBotEntityFromPayload(String payload) {
    var fields = parsePayload(payload, BOT_ENTITY_FIELD, PAYLOAD_FIELDS);
    checkLeastOneField(fields);
    return BotEntity.builder()
        .sellerName(fields.get(SELLER_NAME_FIELD))
        .paymentMethod1(fields.get(PAYMENT_METHOD1_FIELD))
        .paymentMethod2(fields.get(PAYMENT_METHOD2_FIELD))
        .build();
  }
}
