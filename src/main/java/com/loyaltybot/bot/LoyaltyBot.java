package com.loyaltybot.bot;

import com.loyaltybot.config.TelegramBotConfig;
import com.loyaltybot.entity.Business;
import com.loyaltybot.entity.Coupon;
import com.loyaltybot.entity.User;
import com.loyaltybot.entity.UserCoupon;
import com.loyaltybot.service.BusinessService;
import com.loyaltybot.service.CouponService;
import com.loyaltybot.service.QrCodeService;
import com.loyaltybot.service.UserCouponService;
import com.loyaltybot.service.UserService;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class LoyaltyBot extends TelegramLongPollingBot {

    private final TelegramBotConfig botConfig;
    private final UserService userService;
    private final BusinessService businessService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final QrCodeService qrCodeService;

    public LoyaltyBot(TelegramBotConfig botConfig, UserService userService, 
                      BusinessService businessService, CouponService couponService,
                      UserCouponService userCouponService, QrCodeService qrCodeService) {
        this.botConfig = botConfig;
        this.userService = userService;
        this.businessService = businessService;
        this.couponService = couponService;
        this.userCouponService = userCouponService;
        this.qrCodeService = qrCodeService;
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
            if (messageText.equals("/start")) {
                handleStart(chatId, userId, firstName);
            } else if (messageText.equals("/help")) {
                handleHelp(chatId);
            } else if (messageText.equals("/mycoupons")) {
                handleMyCoupons(chatId, userId);
            } else if (messageText.equals("/createbusiness")) {
                handleCreateBusiness(chatId, userId);
            } else if (messageText.startsWith("/activatecoupon ")) {
                handleActivateCoupon(chatId, userId, messageText);
            } else if (messageText.startsWith("/showqr ")) {
                handleShowQr(chatId, userId, messageText);
            } else if (messageText.startsWith("/createbusiness ")) {
                handleCreateBusinessWithName(chatId, userId, messageText);
            } else {
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
            "/createbusiness <Название> — создать бизнес (для владельцев)%n%n" +
            "Готов начать? Используй /mycoupons чтобы увидеть доступные купоны!",
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
            "*Команды:*%n" +
            "/mycoupons — показать мои купоны%n" +
            "/showqr <ID> — показать QR-код купона%n%n" +
            "*Для бизнеса:*%n" +
            "/createbusiness <Название> — создать бизнес%n" +
            "Сканируй QR-коды клиентов для добавления печатей%n%n" +
            "Вопросы? Пиши @xmlreader";
        sendMessage(chatId, text);
    }

    private void handleMyCoupons(Long chatId, Long userId) {
        try {
            List<UserCoupon> userCoupons = userCouponService.findActiveCoupons(userId);
            
            if (userCoupons.isEmpty()) {
                String text = "🎫 *Твои купоны*%n%n" +
                    "У тебя пока нет активных купонов.%n%n" +
                    "Попроси заведение активировать купон или используй команду:%n" +
                    "/activatecoupon <ID_купона>%n%n" +
                    "_Пример: /activatecoupon 1_";
                sendMessage(chatId, text);
                return;
            }
            
            StringBuilder sb = new StringBuilder("🎫 *Твои купоны:*%n%n");
            for (UserCoupon uc : userCoupons) {
                String progress = userCouponService.getProgressText(uc.getId());
                sb.append(String.format("📍 *%s*%n%s%nID: `%d`%n%n", 
                    uc.getCoupon().getName(), 
                    progress.replace("\n", "%n"),
                    uc.getId()));
            }
            sb.append("%nИспользуй /showqr <ID> чтобы показать QR-код");
            
            sendMessage(chatId, sb.toString());
            
        } catch (Exception e) {
            log.error("Error in handleMyCoupons", e);
            sendMessage(chatId, "❌ Произошла ошибка. Попробуй позже.");
        }
    }

    private void handleShowQr(Long chatId, Long userId, String messageText) {
        try {
            Long userCouponId = Long.parseLong(messageText.replace("/showqr ", "").trim());
            
            UserCoupon userCoupon = userCouponService.findActiveCoupons(userId).stream()
                .filter(uc -> uc.getId().equals(userCouponId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Купон не найден или не активен"));
            
            // Генерируем QR-код
            byte[] qrCodeImage = qrCodeService.generateUserCouponQr(userCouponId);
            
            // Отправляем изображение
            sendPhoto(chatId, qrCodeImage, 
                String.format("🎫 *%s*%n%s%n%nОтсканируй этот код на кассе", 
                    userCoupon.getCoupon().getName(),
                    userCouponService.getProgressText(userCouponId).replace("\n", "%n")));
            
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверный формат. Используй: /showqr <ID>");
        } catch (Exception e) {
            log.error("Error in handleShowQr", e);
            sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }
    }

    private void handleActivateCoupon(Long chatId, Long userId, String messageText) {
        try {
            Long couponId = Long.parseLong(messageText.replace("/activatecoupon ", "").trim());
            
            UserCoupon userCoupon = userCouponService.activateCoupon(userId, couponId);
            
            String text = String.format(
                "✅ *Купон активирован!*%n%n" +
                "📍 *%s*%n" +
                "🎁 Награда: %s%n" +
                "📊 Прогресс: 0/%d печатей%n%n" +
                "Используй /mycoupons чтобы увидеть все купоны",
                userCoupon.getCoupon().getName(),
                userCoupon.getCoupon().getRewardDescription(),
                userCoupon.getCoupon().getStampTarget()
            );
            sendMessage(chatId, text);
            
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверный формат. Используй: /activatecoupon <ID>");
        } catch (IllegalStateException e) {
            sendMessage(chatId, "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error in handleActivateCoupon", e);
            sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }
    }

    private void handleCreateBusiness(Long chatId, Long userId) {
        String text = "🏢 *Создание бизнеса*%n%n" +
            "Чтобы создать бизнес, отправь:%n" +
            "/createbusiness <Название>%n%n" +
            "Пример: /createbusiness Coffee House";
        sendMessage(chatId, text);
    }

    private void handleCreateBusinessWithName(Long chatId, Long userId, String messageText) {
        try {
            String businessName = messageText.replace("/createbusiness ", "").trim();
            
            if (businessName.length() < 2) {
                sendMessage(chatId, "❌ Слишком короткое название. Минимум 2 символа.");
                return;
            }
            
            Business business = businessService.createBusiness(businessName, userId);
            
            // Создаём тестовый купон для бизнеса
            Coupon coupon = couponService.createCoupon(
                business.getId(),
                "Бесплатный кофе",
                9,
                "Получи бесплатный кофе после 9 покупок",
                30
            );
            
            String text = String.format(
                "✅ *Бизнес создан!*%n%n" +
                "🏢 Название: *%s*%n" +
                "🆔 ID бизнеса: `%d`%n" +
                "📦 План: %s%n%n" +
                "🎫 *Тестовый купон создан:*%n" +
                "📍 Название: %s%n" +
                "🎁 Награда: %s%n" +
                "📊 Печатей нужно: %d%n" +
                "🆔 ID купона: `%d`%n%n" +
                "Теперь ты можешь:%n" +
                "• Активировать купон себе: /activatecoupon %d%n" +
                "• Дать ID купона клиентам для активации%n%n" +
                "_Скоро появится веб-панель для управления бизнесом_",
                business.getName(),
                business.getId(),
                business.getPlan(),
                coupon.getName(),
                coupon.getRewardDescription(),
                coupon.getStampTarget(),
                coupon.getId(),
                coupon.getId()
            );
            sendMessage(chatId, text);
            
        } catch (IllegalStateException e) {
            sendMessage(chatId, "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error in handleCreateBusinessWithName", e);
            sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }
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

    private void sendPhoto(Long chatId, byte[] imageData, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(imageData), "qrcode.png"));
        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("Markdown");
        
        try {
            execute(sendPhoto);
            log.info("Photo sent to {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo to {}: {}", chatId, e.getMessage());
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
