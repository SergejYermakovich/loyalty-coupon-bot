package com.loyaltybot.service;

import com.loyaltybot.entity.Business;
import com.loyaltybot.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessService {

    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public Optional<Business> findByTelegramId(Long telegramId) {
        return businessRepository.findByOwnerTelegramId(telegramId);
    }

    @Transactional(readOnly = true)
    public List<Business> findAllActive() {
        return businessRepository.findByActiveTrue();
    }

    public Business createBusiness(String name, Long ownerTelegramId) {
        if (businessRepository.existsByOwnerTelegramId(ownerTelegramId)) {
            throw new IllegalStateException("Business already exists for this Telegram user");
        }
        
        Business business = new Business();
        business.setName(name);
        business.setOwnerTelegramId(ownerTelegramId);
        business.setPlan("starter");
        business.setActive(true);
        
        return businessRepository.save(business);
    }

    public Business updatePlan(Long businessId, String plan) {
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new RuntimeException("Business not found"));
        
        business.setPlan(plan);
        return businessRepository.save(business);
    }

    public void deactivateBusiness(Long businessId) {
        Business business = businessRepository.findById(businessId)
            .orElseThrow(() -> new RuntimeException("Business not found"));
        business.setActive(false);
        businessRepository.save(business);
    }
}
