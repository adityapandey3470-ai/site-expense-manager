package com.aditya.siteexpensemanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
public class SystemSettings {

    @Id
    private Long id = 1L;

    @Column(name = "food_rate_per_person", nullable = false)
    private BigDecimal foodRatePerPerson;

    @Column(name = "payout_cycle_days", nullable = false)
    private Integer payoutCycleDays;
}