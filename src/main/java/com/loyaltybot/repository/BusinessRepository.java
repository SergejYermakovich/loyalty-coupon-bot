package com.loyaltybot.repository;

import com.loyaltybot.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    
    Optional<Business> findByOwnerTelegramId(Long telegramId);
    
    List<Business> findByActiveTrue();
    
    boolean existsByOwnerTelegramId(Long telegramId);
}
