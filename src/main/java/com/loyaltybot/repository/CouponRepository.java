package com.loyaltybot.repository;

import com.loyaltybot.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    List<Coupon> findByBusinessIdAndActiveTrue(Long businessId);
    
    List<Coupon> findByBusinessId(Long businessId);
}
