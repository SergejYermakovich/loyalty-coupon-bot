package com.loyaltybot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Business extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private Long ownerTelegramId;

    @Column(nullable = false)
    private String plan = "starter"; // starter, pro, business

    @Column
    private String timezone = "Europe/Minsk";

    @Column
    private Boolean active = true;

    @Column
    private LocalDateTime subscriptionEndsAt;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<StaffMember> staffMembers = new ArrayList<>();
}
