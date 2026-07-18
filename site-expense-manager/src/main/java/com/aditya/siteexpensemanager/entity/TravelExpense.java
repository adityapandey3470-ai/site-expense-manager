package com.aditya.siteexpensemanager.entity;

import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.enums.TravelMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "travel_expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_code", nullable = false, unique = true)
    private String travelCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Column(name = "from_location", nullable = false)
    private String fromLocation;

    @Column(name = "to_location", nullable = false)
    private String toLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_mode", nullable = false)
    private TravelMode travelMode;

    @Column(name = "travel_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal travelCost;

    @Column(name = "travel_purpose", nullable = false)
    private String travelPurpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_status", nullable = false)
    private TravelExpenseStatus travelStatus = TravelExpenseStatus.PENDING;

    @Column(name = "remarks")
    private String remarks;

    // Mandatory: cab/travel invoice photo or PDF must be attached before submission.
    @Column(name = "bill_attached", nullable = false)
    private Boolean billAttached = false;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}