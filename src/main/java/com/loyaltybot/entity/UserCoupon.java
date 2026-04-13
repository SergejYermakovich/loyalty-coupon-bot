package com.loyaltybot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_coupons", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCoupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Coupon coupon;

    @Column(nullable = false)
    private Integer stamps = 0;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime claimedAt;

    @Column
    private Boolean active = true;

    @OneToMany(mappedBy = "userCoupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Visit> visits = new ArrayList<>();

    public void addStamp() {
        this.stamps++;
        if (this.stamps >= this.coupon.getStampTarget()) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
