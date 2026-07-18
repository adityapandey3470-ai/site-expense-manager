package com.aditya.siteexpensemanager.dto.response;

import com.aditya.siteexpensemanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String fullName;
    private Role role;
    private Long siteId;

    public JwtResponseDto(String token, Long userId, String username, String fullName, Role role, Long siteId) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.siteId = siteId;
    }
}
