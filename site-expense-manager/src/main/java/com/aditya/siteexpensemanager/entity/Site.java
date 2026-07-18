package com.aditya.siteexpensemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "sites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Site {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false, unique = true)
    private String siteCode;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String projectManager;


    @Column(name = "budget", precision = 15, scale = 2, nullable = false)
    private BigDecimal budget;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Number of workers assigned to this site; drives default food accrual and payout calculation.
    @Column(nullable = false)
    private Integer teamSize;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;




}
