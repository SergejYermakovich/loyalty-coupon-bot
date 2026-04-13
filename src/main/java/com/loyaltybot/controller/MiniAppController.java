package com.loyaltybot.controller;

import com.loyaltybot.dto.ActivityDTO;
import com.loyaltybot.dto.BusinessStatsDTO;
import com.loyaltybot.dto.CouponInfoDTO;
import com.loyaltybot.dto.StampRequestDTO;
import com.loyaltybot.entity.Business;
import com.loyaltybot.entity.Coupon;
import com.loyaltybot.entity.User;
import com.loyaltybot.entity.UserCoupon;
import com.loyaltybot.entity.Visit;
import com.loyaltybot.repository.BusinessRepository;
import com.loyaltybot.repository.UserCouponRepository;
import com.loyaltybot.repository.UserRepository;
import com.loyaltybot.repository.VisitRepository;
import com.loyaltybot.service.BusinessStatsService;
import com.loyaltybot.service.UserCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Для Telegram Mini Apps
public class MiniAppController {

    private final BusinessStatsService businessStatsService;
    private final UserCouponService userCouponService;
    private final UserCouponRepository userCouponRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;

    /**
     * Получить статистику бизнеса
     * GET /api/business/stats
     */
    @GetMapping("/business/stats")
    public ResponseEntity<BusinessStatsDTO> getBusinessStats(
            @RequestParam(required = false) Long businessId) {
        
        // Для MVP используем первый активный бизнес
        // В реальности нужно извлекать из Telegram initData
        if (businessId == null) {
            businessId = businessRepository.findAll().stream()
                .findFirst()
                .map(Business::getId)
                .orElseThrow(() -> ApiException.notFound("No business found"));
        }
        
        BusinessStatsDTO stats = businessStatsService.getBusinessStats(businessId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Получить информацию о купоне
     * GET /api/coupons/{userCouponId}
     */
    @GetMapping("/coupons/{userCouponId}")
    public ResponseEntity<CouponInfoDTO> getCouponInfo(@PathVariable Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> ApiException.notFound("UserCoupon"));
        
        User user = userCoupon.getUser();
        String userName = user.getFirstName() != null ? 
            user.getFirstName() : 
            (user.getUsername() != null ? user.getUsername() : "Клиент");
        
        CouponInfoDTO dto = CouponInfoDTO.builder()
            .userCouponId(userCoupon.getId())
            .couponId(userCoupon.getCoupon().getId())
            .couponName(userCoupon.getCoupon().getName())
            .stamps(userCoupon.getStamps())
            .stampTarget(userCoupon.getCoupon().getStampTarget())
            .rewardDescription(userCoupon.getCoupon().getRewardDescription())
            .completed(userCoupon.getCompletedAt() != null)
            .claimed(userCoupon.getClaimedAt() != null)
            .userId(user.getId())
            .userName(userName)
            .businessId(userCoupon.getCoupon().getBusiness().getId())
            .build();
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Добавить печать
     * POST /api/stamps/add
     */
    @PostMapping("/stamps/add")
    public ResponseEntity<Map<String, Object>> addStamp(
            @Valid @RequestBody StampRequestDTO request) {
        
        log.info("Adding stamp for userCouponId: {}", request.getUserCouponId());
        
        try {
            UserCoupon userCoupon = userCouponService.addStamp(
                request.getUserCouponId(),
                request.getStaffMemberId(),
                request.getLocation()
            );
            
            // Сохраняем посещение
            Visit visit = new Visit();
            visit.setUserCoupon(userCoupon);
            visit.setStaffMemberId(request.getStaffMemberId());
            visit.setLocation(request.getLocation());
            visit.setVisitTime(LocalDateTime.now());
            visitRepository.save(visit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userCouponId", userCoupon.getId());
            response.put("newStamps", userCoupon.getStamps());
            response.put("stampTarget", userCoupon.getCoupon().getStampTarget());
            response.put("completed", userCoupon.getCompletedAt() != null);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            throw ApiException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to add stamp", e);
            throw ApiException.badRequest("Failed to add stamp");
        }
    }

    /**
     * Забрать награду
     * POST /api/coupons/{userCouponId}/claim
     */
    @PostMapping("/coupons/{userCouponId}/claim")
    public ResponseEntity<Map<String, Object>> claimReward(@PathVariable Long userCouponId) {
        try {
            UserCoupon userCoupon = userCouponService.claimReward(userCouponId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("claimedAt", userCoupon.getClaimedAt());
            response.put("reward", userCoupon.getCoupon().getRewardDescription());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            throw ApiException.badRequest(e.getMessage());
        }
    }

    /**
     * Получить список купонов бизнеса
     * GET /api/business/coupons
     */
    @GetMapping("/business/coupons")
    public ResponseEntity<List<Map<String, Object>>> getBusinessCoupons(
            @RequestParam(required = false) Long businessId) {
        
        if (businessId == null) {
            businessId = businessRepository.findAll().stream()
                .findFirst()
                .map(Business::getId)
                .orElseThrow(() -> ApiException.notFound("No business found"));
        }
        
        List<Coupon> coupons = businessRepository.findById(businessId)
            .map(Business::getCoupons)
            .orElseThrow(() -> ApiException.notFound("Business"))
            .stream()
            .filter(Coupon::getActive)
            .collect(Collectors.toList());
        
        List<Map<String, Object>> result = coupons.stream().map(coupon -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", coupon.getId());
            dto.put("name", coupon.getName());
            dto.put("stampTarget", coupon.getStampTarget());
            dto.put("rewardDescription", coupon.getRewardDescription());
            dto.put("validityDays", coupon.getValidityDays());
            
            // Count active users
            long activeUsers = coupon.getUserCoupons().stream()
                .filter(UserCoupon::getActive)
                .count();
            dto.put("activeUsers", activeUsers);
            
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    /**
     * Получить последние посещения
     * GET /api/business/visits
     */
    @GetMapping("/business/visits")
    public ResponseEntity<List<ActivityDTO>> getRecentVisits(
            @RequestParam(required = false) Long businessId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        if (businessId == null) {
            businessId = businessRepository.findAll().stream()
                .findFirst()
                .map(Business::getId)
                .orElseThrow(() -> ApiException.notFound("No business found"));
        }
        
        BusinessStatsDTO stats = businessStatsService.getBusinessStats(businessId);
        List<ActivityDTO> activity = stats.getRecentActivity()
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(activity);
    }
}
