package com.loyaltybot.repository;

import com.loyaltybot.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    
    List<UserCoupon> findByUserIdAndActiveTrue(Long userId);
    
    List<UserCoupon> findByCouponIdAndActiveTrue(Long couponId);
    
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
    
    List<UserCoupon> findByUserIdAndCompletedAtIsNull(Long userId);
}
