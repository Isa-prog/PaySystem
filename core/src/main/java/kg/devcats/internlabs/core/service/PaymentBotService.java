package kg.devcats.internlabs.core.service;

import kg.devcats.internlabs.core.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentBotService extends TelegramLongPollingBot {
    private static Logger logger = LoggerFactory.getLogger(PaymentBotService.class);

    final BotConfig config;
    private Long currentChatId;

    public PaymentBotService(BotConfig config){
        this.config = config;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getToken();
    }

    public void sendNotification(String message) {
        Long chatId = this.currentChatId;
        sendMessageToTelegram(chatId, message);
    }

    private void sendMessageToTelegram(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId((String.valueOf(chatId)));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to Telegram: {}", e.getMessage());
        }
    }

    public void onUpdateReceived(Update update) {
        if (update != null && update.hasMessage() && update.getMessage().isCommand()){
            long chatId = update.getMessage().getChatId();
            startCommandRecived(chatId, update.getMessage().getChat().getUserName());
        } else {
            logger.error("Failed to send success notification: Update is null or does not contain a message");
        }
    }

    private void startCommandRecived(long chatId, String name) {
        this.currentChatId = chatId;
        String answer = "Добро пожаловать, " + name + ". Вас приветствует платежная система!";
        sendStartButtonOnly(chatId);
        logger.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }catch (TelegramApiException e){
            logger.error("Error occurred " + e.getMessage());
        }
    }
    public void sendStartButtonOnly(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы запустили бота");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("/start");

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

