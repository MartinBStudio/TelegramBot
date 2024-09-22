package cz.raadost.service;

import cz.raadost.dataSource.ContentEntity;
import cz.raadost.dataSource.ContentService;
import cz.raadost.model.StaticMessages;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@PropertySource("classpath:telegram.properties")
public class Messanger extends TelegramLongPollingBot {
    private final ContentService contentService;

    private final Long NOTIFICATION_CHANNEL_ID;
    private final String BOT_TOKEN;
    private final String BOT_USERNAME;
    @Autowired
    public Messanger(
            ContentService contentService, @Value("${telegram.bot.notification.channel.id}") Long NOTIFICATION_CHANNEL_ID,
            @Value("${telegram.bot.token}") String BOT_TOKEN,
            @Value("${telegram.bot.username}") String BOT_USERNAME) {
        this.contentService = contentService;
        this.NOTIFICATION_CHANNEL_ID = NOTIFICATION_CHANNEL_ID;
        this.BOT_TOKEN = BOT_TOKEN;
        this.BOT_USERNAME = BOT_USERNAME;
    }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, StaticMessages.WELCOME.getMessage());
                    break;

                case "/obsah":
                    sendMessage(chatId, buildAllContentListMessage());
                    break;

                default:
                    handleCustomMessages(messageText, chatId, user);
                    break;
            }
        }
    }
    private void handleCustomMessages(String messageText, Long chatId, User user) {
        if (messageText.matches("/\\d+")) { // Check if the message contains only digits
            String numberPart = messageText.substring(1);
            handleNumberMessage(numberPart, chatId, user);
        } else if (messageText.matches("/ZAPLACENO\\d+")) { // Check if the message is "ZAPLACENO" followed by a number
            handleZaplacenoMessage(messageText, chatId, user);
        } else {
            sendMessage(chatId, "Této odpovědí nerozumím :(.");
        }
    }
    private void handleNumberMessage(String messageText, Long chatId, User user) {
        int messageNumber = Integer.parseInt(messageText);
        if (messageNumber <= contentService.findAll().size()) {
            sendMessage(chatId, buildContentMessageFronStringIndex(String.valueOf(messageNumber), user.getId()));
        } else {
            sendMessage(chatId, StaticMessages.CONTENT_OUT_OF_BOUNDS.getMessage());
        }
    }
    private void handleZaplacenoMessage(String messageText, Long chatId, User user) {
        String numberString = messageText.replaceAll("[^0-9]", "");
        int number = Integer.parseInt(numberString);

        if (number <= contentService.findAll().size()) {
            var data = contentService.findById(number);
            String channelMessageText = String.format("Uživatel - @%s\nObsah - [%s] %s\nČástka - %sCZK\nPoznámka k platbě - %s\n\nOvěř platbu a uživatele kontaktuj!",
                    user.getFirstName()+user.getLastName(), data.getContentIndex(), data.getName() ,data.getPrice(), user.getId());
            sendMessage(NOTIFICATION_CHANNEL_ID, channelMessageText);
            sendMessage(chatId, StaticMessages.THANKS_MESSAGE.getMessage());
        }
    }
    private String buildAllContentListMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("Vyber si kliknutím na číslo:\n\n");
        for(ContentEntity data:contentService.findAll()){
            sb.append(String.format("/%s - %s - %s CZK\n",data.getContentIndex(),data.getName(),data.getPrice()));
        }


        return sb.toString();
    }
    private String buildContentMessageFronStringIndex(String index,Long userId){
        var selectedData = contentService.findById(Long.parseLong(index));
        var contentSelected = StaticMessages.CONTENT_SELECTED.getMessage();
        var contentIndex = selectedData.getContentIndex();
        var contentName = selectedData.getName();
        var contentType = selectedData.getType();
        var contentDescription = selectedData.getDescription();
        var contentPrice = selectedData.getPrice();
        var paymentDetails = StaticMessages.PAYMENT_DETAILS.getMessage();
        var paymentGuide = StaticMessages.PAYMENT_GUIDE.getMessage();

        return String.format(" %s\n\n%s\n DRUH - %s\n POPIS - %s\n CENA - %sCZK\n\n %s %s\n\n%s%s",contentSelected,contentName,contentType,contentDescription,contentPrice,paymentDetails,userId,paymentGuide,contentIndex);
    }
    private void sendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
