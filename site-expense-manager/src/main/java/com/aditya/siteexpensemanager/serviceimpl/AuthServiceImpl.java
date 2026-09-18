package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.ChangePasswordRequestDto;
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
import com.aditya.siteexpensemanager.security.JwtUtil;
import com.aditya.siteexpensemanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;




    @Override
    @Transactional
    public synchronized UserResponseDto register(RegisterRequestDto requestDto) {

            if (userRepository.count() == 0) {

                if (requestDto.getRole() == Role.SUPERVISOR && requestDto.getSiteId() == null) {
                    throw new IllegalArgumentException("Site id is required for SUPERVISOR role");
                }

                Site bootstrapSite = null;
                if (requestDto.getRole() == Role.SUPERVISOR) {
                    bootstrapSite = siteRepository.findByIdAndDeletedFalse(requestDto.getSiteId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Site not found with id: " + requestDto.getSiteId()));
                }

                User firstUser = new User();
                firstUser.setFullName(requestDto.getFullName());
                firstUser.setUsername(requestDto.getUsername());
                firstUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                firstUser.setRole(requestDto.getRole());
                firstUser.setSite(bootstrapSite);
                firstUser.setActive(true);
                firstUser.setDeleted(false);

                return toResponseDto(userRepository.save(firstUser));
            }

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



    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;


    @Override
    @Transactional
    public JwtResponseDto login(LoginRequestDto requestDto) {

        String username = requestDto.getUsername().trim();

        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElse(null);

        if (user != null && user.getLockedUntil() != null
                && user.getLockedUntil().isAfter(LocalDateTime.now())) {

            long minutesLeft = java.time.Duration.between(
                    LocalDateTime.now(), user.getLockedUntil()).toMinutes() + 1;

            throw new IllegalStateException(
                    "Too many failed attempts. This account is locked for "
                            + minutesLeft + " more minute(s). Contact your Director if you need help."
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username, requestDto.getPassword()
                    )
            );
        } catch (Exception ex) {

            logger.warn("Login failed for username '{}': {} - {}",
                    username, ex.getClass().getSimpleName(), ex.getMessage());

            if (user != null) {
                int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
                }
                userRepository.save(user);
            }

            throw new BadCredentialsException("Invalid username or password");
        }

        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }

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


    @Transactional
    @Override
    public void changePassword(Long userId, ChangePasswordRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
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
