package com.aditya.siteexpensemanager.dto.response;

import com.aditya.siteexpensemanager.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String fullName;
    private String username;
    private Role role;
    private Long siteId;
    private String siteName;
    private Boolean active;
}
