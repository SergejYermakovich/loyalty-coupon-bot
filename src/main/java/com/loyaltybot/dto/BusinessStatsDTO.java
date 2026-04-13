package com.loyaltybot.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BusinessStatsDTO {
    
    private Long businessId;
    private String businessName;
    private Integer totalCustomers;
    private Integer activeCoupons;
    private Integer rewardsClaimed;
    private Integer totalVisits;
    private List<ActivityDTO> recentActivity;
}
