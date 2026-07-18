package com.aditya.siteexpensemanager.enums;

// Only used for EMERGENCY / MATERIAL requests, which need two approvals before
// they're final. TRAVEL_EXPENSE / ADVANCE / REIMBURSEMENT / OTHER requests skip
// this and go straight through the existing single-step RequestStatus flow.
public enum ApprovalStage {
    PENDING_OPERATIONS,
    PENDING_ACCOUNTS_DIRECTOR,
    DONE
}
