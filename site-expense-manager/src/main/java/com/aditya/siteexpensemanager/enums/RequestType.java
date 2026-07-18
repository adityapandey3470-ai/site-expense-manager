package com.aditya.siteexpensemanager.enums;

public enum RequestType {
    TRAVEL_EXPENSE,
    ADVANCE,
    REIMBURSEMENT,
    OTHER,
    // Go through the two-step Operations -> Accounts/Director approval chain.
    EMERGENCY,
    MATERIAL
}
