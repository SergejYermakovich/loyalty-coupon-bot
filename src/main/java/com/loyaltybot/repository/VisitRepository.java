package com.loyaltybot.repository;

import com.loyaltybot.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    
    List<Visit> findByUserCouponIdOrderByVisitTimeDesc(Long userCouponId);
    
    List<Visit> findByStaffMemberIdOrderByVisitTimeDesc(Long staffMemberId);
}
