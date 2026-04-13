package com.loyaltybot.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StampRequestDTO {
    
    @NotNull(message = "userCouponId is required")
    private Long userCouponId;
    
    private Long staffMemberId;
    
    private String location;
}
