package com.aditya.siteexpensemanager.dto.response;

import com.aditya.siteexpensemanager.enums.ApprovalStage;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.RequestType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RequestResponseDto {

    private Long id;

    private String requestCode;

    private Long siteId;

    private Long travelExpenseId;

    private String requestedBy;

    private RequestType requestType;

    private String description;

    private BigDecimal amount;

    private RequestStatus status;

    private ApprovalStage approvalStage;

    private String approverName;

    private String rejectionReason;

    private LocalDate requestDate;

    private LocalDate actionDate;

    private boolean active;
}
