package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.LoginRequestDto;
import com.aditya.siteexpensemanager.dto.request.RegisterRequestDto;
import com.aditya.siteexpensemanager.dto.response.JwtResponseDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.User;
import com.aditya.siteexpensemanager.enums.Role;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.UserRepository;
import com.aditya.siteexpensemanager.security.CustomUserDetails;
import com.aditya.siteexpensemanager.security.JwtUtil;
import com.aditya.siteexpensemanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;




    @Override
    @Transactional
    public UserResponseDto register(RegisterRequestDto requestDto) {

            if (requestDto.getRole() != Role.SUPERVISOR) {
                throw new IllegalArgumentException(
                        "Self-registration is only allowed for SUPERVISOR. "
                                + "Contact a DIRECTOR to create accounts for other roles."
                );
            }

            if (userRepository.existsByUsername(requestDto.getUsername())) {
                throw new IllegalStateException(
                        "Username already taken: " + requestDto.getUsername()
                );
            }


        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalStateException(
                    "Username already taken: " + requestDto.getUsername()
            );
        }

        Site site = null;

        if (requestDto.getRole() == Role.SUPERVISOR) {

            if (requestDto.getSiteId() == null) {
                throw new IllegalArgumentException(
                        "Site id is required for SUPERVISOR role"
                );
            }

            site = siteRepository.findByIdAndDeletedFalse(requestDto.getSiteId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Site not found with id: " + requestDto.getSiteId()
                            )
                    );
        } else if (requestDto.getSiteId() != null) {
            throw new IllegalArgumentException(
                    "Site id is allowed only for SUPERVISOR role"
            );
        }

        User user = new User();
        user.setFullName(requestDto.getFullName());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(requestDto.getRole());
        user.setSite(site);
        user.setActive(true);
        user.setDeleted(false);

        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }



    @Override
    public JwtResponseDto login(LoginRequestDto requestDto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(), requestDto.getPassword()
                    )
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsernameAndDeletedFalse(requestDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.getActive()) {
            throw new IllegalStateException("User account is deactivated");
        }

        Long siteId = user.getSite() != null ? user.getSite().getId() : null;

        String token = jwtUtil.generateToken(
                user.getUsername(), user.getId(), user.getRole().name(), siteId
        );

        return new JwtResponseDto(
                token, user.getId(), user.getUsername(), user.getFullName(), user.getRole(), siteId
        );
    }

    private UserResponseDto toResponseDto(User user) {

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setFullName(user.getFullName());
        responseDto.setUsername(user.getUsername());
        responseDto.setRole(user.getRole());
        responseDto.setActive(user.getActive());

        if (user.getSite() != null) {
            responseDto.setSiteId(user.getSite().getId());
            responseDto.setSiteName(user.getSite().getSiteName());
        }

        return responseDto;
    }

    @Override
    @Transactional
    public UserResponseDto registerPrivileged(RegisterRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalStateException(
                    "Username already taken: " + requestDto.getUsername()
            );
        }

        Site site = null;

        if (requestDto.getRole() == Role.SUPERVISOR) {
            if (requestDto.getSiteId() == null) {
                throw new IllegalArgumentException("Site id is required for SUPERVISOR role");
            }
            site = siteRepository.findByIdAndDeletedFalse(requestDto.getSiteId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Site not found with id: " + requestDto.getSiteId()));
        } else if (requestDto.getSiteId() != null) {
            throw new IllegalArgumentException("Site id is allowed only for SUPERVISOR role");
        }

        User user = new User();
        user.setFullName(requestDto.getFullName());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(requestDto.getRole());
        user.setSite(site);
        user.setActive(true);
        user.setDeleted(false);

        return toResponseDto(userRepository.save(user));
    }
}
