package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.ResetPasswordRequestDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;
import com.aditya.siteexpensemanager.entity.User;
import com.aditya.siteexpensemanager.enums.Role;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.repository.UserRepository;
import com.aditya.siteexpensemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> listUsers() {

        return userRepository.findAll()
                .stream()
                .filter(u -> !u.getDeleted())
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, ResetPasswordRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponseDto toggleActive(Long targetUserId, Long actingUserId, String confirmPassword) {

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (target.getRole() == Role.DIRECTOR) {

            User actingUser = userRepository.findById(actingUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Acting user not found"));

            if (confirmPassword == null
                    || !passwordEncoder.matches(confirmPassword, actingUser.getPassword())) {
                throw new BadCredentialsException("Incorrect password. Action cancelled for safety.");
            }
        }

        target.setActive(!target.getActive());
        User saved = userRepository.save(target);

        return toResponseDto(saved);
    }

    private UserResponseDto toResponseDto(User user) {

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());

        if (user.getSite() != null) {
            dto.setSiteId(user.getSite().getId());
            dto.setSiteName(user.getSite().getSiteName());
        }

        return dto;
    }

    @Override
    @Transactional
    public void deleteUser(Long targetUserId, Long actingUserId) {

        if (targetUserId.equals(actingUserId)) {
            throw new IllegalStateException("You cannot delete your own account.");
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (target.getRole() == Role.DIRECTOR) {
            long activeDirectorCount = userRepository.findAll().stream()
                    .filter(u -> !u.getDeleted())
                    .filter(u -> u.getRole() == Role.DIRECTOR)
                    .count();

            if (activeDirectorCount <= 1) {
                throw new IllegalStateException("Cannot delete the last remaining Director account.");
            }
        }

        target.setDeleted(true);
        target.setActive(false);
        userRepository.save(target);
    }
}
