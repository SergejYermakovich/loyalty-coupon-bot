package com.loyaltybot.service;

import com.loyaltybot.dto.ActivityDTO;
import com.loyaltybot.dto.BusinessStatsDTO;
import com.loyaltybot.entity.Business;
import com.loyaltybot.entity.Coupon;
import com.loyaltybot.entity.UserCoupon;
import com.loyaltybot.entity.Visit;
import com.loyaltybot.repository.BusinessRepository;
import com.loyaltybot.repository.CouponRepository;
import com.loyaltybot.repository.UserCouponRepository;
import com.loyaltybot.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BusinessStatsService {

    private final BusinessRepository businessRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final VisitRepository visitRepository;

    /**
     * Получить статистику бизнеса
     */
    public BusinessStatsDTO getBusinessStats(Long businessId) {
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new RuntimeException("Business not found"));
        
        // Активные купоны бизнеса
        List<Coupon> activeCoupons = couponRepository.findByBusinessIdAndActiveTrue(businessId);
        List<Long> couponIds = activeCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        
        // Все активные user coupons
        List<UserCoupon> activeUserCoupons = userCouponRepository.findAll()
            .stream()
            .filter(uc -> uc.getActive() && couponIds.contains(uc.getCoupon().getId()))
            .collect(Collectors.toList());
        
        // Посещения за последние 7 дней
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Visit> recentVisits = visitRepository.findAll()
            .stream()
            .filter(v -> v.getVisitTime().isAfter(sevenDaysAgo))
            .filter(v -> v.getUserCoupon() != null && 
                   v.getUserCoupon().getCoupon() != null &&
                   v.getUserCoupon().getCoupon().getBusiness().getId().equals(businessId))
            .collect(Collectors.toList());
        
        // Уникальные клиенты
        long uniqueCustomers = activeUserCoupons.stream()
            .map(uc -> uc.getUser().getId())
            .distinct()
            .count();
        
        // Выданные награды
        long rewardsClaimed = userCouponRepository.findAll()
            .stream()
            .filter(uc -> uc.getCoupon() != null && 
                   uc.getCoupon().getBusiness().getId().equals(businessId) &&
                   uc.getClaimedAt() != null)
            .count();
        
        // Recent activity
        List<ActivityDTO> recentActivity = buildRecentActivity(recentVisits, activeUserCoupons);
        
        return BusinessStatsDTO.builder()
            .businessId(businessId)
            .businessName(business.getName())
            .totalCustomers((int) uniqueCustomers)
            .activeCoupons(activeUserCoupons.size())
            .rewardsClaimed((int) rewardsClaimed)
            .totalVisits(recentVisits.size())
            .recentActivity(recentActivity)
            .build();
    }

    /**
     * Построить список активности
     */
    private List<ActivityDTO> buildRecentActivity(List<Visit> visits, List<UserCoupon> userCoupons) {
        return visits.stream()
            .limit(10)
            .map(visit -> {
                UserCoupon uc = visit.getUserCoupon();
                String userName = uc.getUser().getFirstName() != null ? 
                    uc.getUser().getFirstName() : "Клиент";
                
                return ActivityDTO.builder()
                    .type("stamp_added")
                    .title(String.format("%s получил печать", userName))
                    .description(uc.getCoupon().getName())
                    .timestamp(visit.getVisitTime())
                    .userId(uc.getUser().getId())
                    .userName(userName)
                    .couponId(uc.getCoupon().getId())
                    .couponName(uc.getCoupon().getName())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
