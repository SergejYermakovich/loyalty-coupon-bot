package com.loyaltybot.service;

import com.loyaltybot.entity.Coupon;
import com.loyaltybot.entity.Business;
import com.loyaltybot.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<Coupon> findByBusinessId(Long businessId) {
        return couponRepository.findByBusinessIdAndActiveTrue(businessId);
    }

    public Coupon createCoupon(Long businessId, String name, Integer stampTarget, 
                                String rewardDescription, Integer validityDays) {
        Coupon coupon = new Coupon();
        
        Business business = new Business();
        business.setId(businessId);
        coupon.setBusiness(business);
        
        coupon.setName(name);
        coupon.setStampTarget(stampTarget != null ? stampTarget : 10);
        coupon.setRewardDescription(rewardDescription);
        coupon.setValidityDays(validityDays != null ? validityDays : 30);
        coupon.setActive(true);
        
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long couponId, String name, Integer stampTarget, 
                                String rewardDescription, Integer validityDays) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        if (name != null) coupon.setName(name);
        if (stampTarget != null) coupon.setStampTarget(stampTarget);
        if (rewardDescription != null) coupon.setRewardDescription(rewardDescription);
        if (validityDays != null) coupon.setValidityDays(validityDays);
        
        return couponRepository.save(coupon);
    }

    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }
}
