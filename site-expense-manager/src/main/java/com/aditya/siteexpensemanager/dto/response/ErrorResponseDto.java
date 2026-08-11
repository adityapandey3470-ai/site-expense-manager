package com.aditya.siteexpensemanager.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private int status;
    private String message;

    public ErrorResponseDto(int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}
