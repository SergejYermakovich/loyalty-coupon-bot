package com.loyaltybot.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ActivityDTO {
    
    private String type; // stamp_added, coupon_activated, reward_claimed, etc.
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private Long userId;
    private String userName;
    private Long couponId;
    private String couponName;
}
