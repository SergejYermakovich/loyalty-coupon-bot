package com.loyaltybot.service;

import com.loyaltybot.entity.Coupon;
import com.loyaltybot.entity.User;
import com.loyaltybot.entity.UserCoupon;
import com.loyaltybot.entity.Visit;
import com.loyaltybot.repository.CouponRepository;
import com.loyaltybot.repository.UserCouponRepository;
import com.loyaltybot.repository.UserRepository;
import com.loyaltybot.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final VisitRepository visitRepository;

    /**
     * Активировать купон для пользователя
     */
    public UserCoupon activateCoupon(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        // Проверка: не активирован ли уже
        Optional<UserCoupon> existing = userCouponRepository.findByUserIdAndCouponId(userId, couponId);
        if (existing.isPresent()) {
            if (existing.get().getActive()) {
                throw new IllegalStateException("Coupon already activated for this user");
            }
            // Реактивация
            UserCoupon userCoupon = existing.get();
            userCoupon.setActive(true);
            userCoupon.setStamps(0);
            userCoupon.setCompletedAt(null);
            userCoupon.setClaimedAt(null);
            return userCouponRepository.save(userCoupon);
        }
        
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setStamps(0);
        userCoupon.setActive(true);
        
        return userCouponRepository.save(userCoupon);
    }

    /**
     * Получить все активные купоны пользователя
     */
    @Transactional(readOnly = true)
    public List<UserCoupon> findActiveCoupons(Long userId) {
        return userCouponRepository.findByUserIdAndActiveTrue(userId);
    }

    /**
     * Добавить печать (посещение)
     */
    public UserCoupon addStamp(Long userCouponId, Long staffMemberId, String location) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> new RuntimeException("UserCoupon not found"));
        
        if (!userCoupon.getActive()) {
            throw new IllegalStateException("Coupon is not active");
        }
        
        if (userCoupon.getCompletedAt() != null) {
            throw new IllegalStateException("Coupon already completed");
        }
        
        // Проверка: не было ли посещения недавно (защита от дубликатов)
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        // Можно добавить дополнительную проверку visits
        
        // Добавляем печать
        userCoupon.addStamp();
        
        // Создаём запись о посещении
        Visit visit = new Visit();
        visit.setUserCoupon(userCoupon);
        visit.setStaffMemberId(staffMemberId);
        visit.setLocation(location);
        visit.setVisitTime(LocalDateTime.now());
        visitRepository.save(visit);
        
        return userCouponRepository.save(userCoupon);
    }

    /**
     * Забрать награду (сбросить купон)
     */
    public UserCoupon claimReward(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> new RuntimeException("UserCoupon not found"));
        
        if (userCoupon.getCompletedAt() == null) {
            throw new IllegalStateException("Coupon not yet completed. Need more stamps!");
        }
        
        if (userCoupon.getClaimedAt() != null) {
            throw new IllegalStateException("Reward already claimed");
        }
        
        userCoupon.setClaimedAt(LocalDateTime.now());
        userCoupon.setActive(false); // Деактивируем после получения награды
        
        return userCouponRepository.save(userCoupon);
    }

    /**
     * Проверить, завершён ли купон
     */
    @Transactional(readOnly = true)
    public boolean isCompleted(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
            .map(uc -> uc.getCompletedAt() != null)
            .orElse(false);
    }

    /**
     * Получить прогресс купона
     */
    @Transactional(readOnly = true)
    public String getProgressText(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
            .map(uc -> {
                int stamps = uc.getStamps();
                int target = uc.getCoupon().getStampTarget();
                String reward = uc.getCoupon().getRewardDescription();
                
                if (uc.getCompletedAt() != null) {
                    if (uc.getClaimedAt() != null) {
                        return String.format("✅ Награда получена: %s", reward);
                    } else {
                        return String.format("🎉 Готово! %d/%d — забирай награду: %s", 
                            stamps, target, reward);
                    }
                }
                
                return String.format("📊 Прогресс: %d/%d печатей\n🎁 Награда: %s", 
                    stamps, target, reward);
            })
            .orElse("Купон не найден");
    }

    /**
     * Отменить последнюю печать (для админа)
     */
    public UserCoupon removeLastStamp(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> new RuntimeException("UserCoupon not found"));
        
        if (userCoupon.getStamps() > 0) {
            userCoupon.setStamps(userCoupon.getStamps() - 1);
            if (userCoupon.getCompletedAt() != null && userCoupon.getStamps() < userCoupon.getCoupon().getStampTarget()) {
                userCoupon.setCompletedAt(null); // Сброс завершения
            }
        }
        
        return userCouponRepository.save(userCoupon);
    }
}
