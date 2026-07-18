package com.aditya.siteexpensemanager.entity;

import com.aditya.siteexpensemanager.enums.ApprovalStage;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String requestCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_expense_id")
    private TravelExpense travelExpense;

    @Column(nullable = false)
    private String requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    @Column(nullable = false)
    private String description;

    // Required for EMERGENCY / MATERIAL requests (estimated / actual amount to be posted to ledger).
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    // Only meaningful for EMERGENCY / MATERIAL requests, see ApprovalStage.
    @Enumerated(EnumType.STRING)
    private ApprovalStage approvalStage;

    private String approverName;

    private String rejectionReason;

    private LocalDate requestDate;

    private LocalDate actionDate;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean deleted = false;
}