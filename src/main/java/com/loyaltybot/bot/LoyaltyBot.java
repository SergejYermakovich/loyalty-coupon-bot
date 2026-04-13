package com.loyaltybot.bot;

import com.loyaltybot.config.TelegramBotConfig;
import com.loyaltybot.service.BusinessService;
import com.loyaltybot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class LoyaltyBot extends TelegramLongPollingBot {

    private final TelegramBotConfig botConfig;
    private final UserService userService;
    private final BusinessService businessService;

    public LoyaltyBot(TelegramBotConfig botConfig, UserService userService, BusinessService businessService) {
        this.botConfig = botConfig;
        this.userService = userService;
        this.businessService = businessService;
        log.info("LoyaltyBot initialized with username: {}", botConfig.getUsername());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();

            log.info("Received message from {}: {}", userId, messageText);

            // Создаём или обновляем пользователя
            userService.getOrCreateUser(userId, username, firstName, null, null);

            // Обрабатываем команды
            switch (messageText) {
                case "/start":
                    handleStart(chatId, userId, firstName);
                    break;
                case "/help":
                    handleHelp(chatId);
                    break;
                case "/mycoupons":
                    handleMyCoupons(chatId, userId);
                    break;
                case "/createbusiness":
                    handleCreateBusiness(chatId, userId);
                    break;
                default:
                    sendUnknownCommand(chatId);
            }
        }
    }

    private void handleStart(Long chatId, Long userId, String firstName) {
        String text = String.format(
            "🎟️ *Добро пожаловать в Loyalty Coupon Bot!*%n%n" +
            "Привет, %s! Я помогу тебе собирать электронные печати в любимых заведениях.%n%n" +
            "*Что я умею:*%n" +
            "• Хранить твои купоны с печатями%n" +
            "• Уведомлять о бесплатных наградах%n" +
            "• Показывать историю посещений%n%n" +
            "*Команды:*%n" +
            "/help — помощь%n" +
            "/mycoupons — мои купоны%n" +
            "/createbusiness — создать бизнес (для владельцев)%n",
            firstName != null ? firstName : "друг"
        );
        sendMessage(chatId, text);
    }

    private void handleHelp(Long chatId) {
        String text = "ℹ️ *Помощь*%n%n" +
            "*Для пользователей:*%n" +
            "• Покажи QR-код купона на кассе%n" +
            "• Получи печать после покупки%n" +
            "• Собери нужное количество и получи награду!%n%n" +
            "*Для бизнеса:*%n" +
            "• Создай купон с настройками%n" +
            "• Сканируй QR-коды клиентов%n" +
            "• Отслеживай статистику%n%n" +
            "Вопросы? Пиши @xmlreader";
        sendMessage(chatId, text);
    }

    private void handleMyCoupons(Long chatId, Long userId) {
        String text = "🎫 *Твои купоны*%n%n" +
            "Здесь будет список твоих активных купонов с прогрессом.%n%n" +
            "_Функция в разработке..._";
        sendMessage(chatId, text);
    }

    private void handleCreateBusiness(Long chatId, Long userId) {
        String text = "🏢 *Создание бизнеса*%n%n" +
            "Чтобы создать бизнес, отправь:%n" +
            "/createbusiness <Название>%n%n" +
            "Пример: /createbusiness Coffee House%n%n" +
            "_Функция в разработке..._";
        sendMessage(chatId, text);
    }

    private void sendUnknownCommand(Long chatId) {
        String text = "❌ Неизвестная команда. Используй /help для списка команд.";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");
        
        try {
            execute(message);
            log.info("Message sent to {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
