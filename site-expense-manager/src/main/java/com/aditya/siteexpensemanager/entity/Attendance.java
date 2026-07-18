package com.aditya.siteexpensemanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "attendances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"site_id", "attendance_date"})
})
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private Integer presentCount;

    // Snapshot of the food rate at the time of marking, so historical entries
    // stay correct even if the rate is changed later in application.properties.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal foodRateApplied;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFoodAmount;

    @Column(nullable = false)
    private Boolean deleted = false;
}
