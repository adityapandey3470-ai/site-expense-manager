package com.aditya.siteexpensemanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmActionRequestDto {

    @NotBlank(message = "Please enter your password to confirm this action")
    private String confirmPassword;
}