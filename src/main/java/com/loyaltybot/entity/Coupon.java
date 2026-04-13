package com.loyaltybot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
@Data
@EqualsAndHashCode(callSuper = true)
public class Coupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Business business;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer stampTarget = 10; // например, 10 печатей

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rewardDescription; // например, "Бесплатный кофе"

    @Column
    private Integer validityDays = 30; // срок действия купона в днях

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<UserCoupon> userCoupons = new ArrayList<>();
}
