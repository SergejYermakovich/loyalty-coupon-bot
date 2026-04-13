package com.loyaltybot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponInfoDTO {
    
    private Long userCouponId;
    private Long couponId;
    private String couponName;
    private Integer stamps;
    private Integer stampTarget;
    private String rewardDescription;
    private Boolean completed;
    private Boolean claimed;
    private Long userId;
    private String userName;
    private Long businessId;
}
