package com.loyaltybot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_members")
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Business business;

    @Column
    private Long telegramId; // если у сотрудника есть Telegram

    @Column(nullable = false)
    private String name;

    @Column
    private String role = "staff"; // staff, manager, admin

    @Column
    private Boolean active = true;

    @Column
    private LocalDateTime lastActiveAt;
}
