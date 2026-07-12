package com.aditya.siteexpensemanager.dto.request;

import com.aditya.siteexpensemanager.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRequestDto {

    @NotNull(message = "Site id is required")
    private Long siteId;

    private Long travelExpenseId;

    @NotBlank(message = "Requested by is required")
    private String requestedBy;

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    @NotBlank(message = "Description is required")
    private String description;
}