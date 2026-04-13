package com.loyaltybot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Data
@EqualsAndHashCode(callSuper = true)
public class Visit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private UserCoupon userCoupon;

    @Column
    private Long staffMemberId;

    @Column
    private String location; // геолокация или название заведения

    @Column
    private LocalDateTime visitTime = LocalDateTime.now();

    @Column
    private String notes; // комментарии (например, причина отмены)

    @Column
    private Boolean cancelled = false;
}
