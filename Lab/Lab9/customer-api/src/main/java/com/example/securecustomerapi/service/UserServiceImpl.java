package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.entity.RefreshToken;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.entity.enums.Role;
import com.example.securecustomerapi.exception.DuplicateResourceException;
import com.example.securecustomerapi.exception.InvalidPasswordException;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.RefreshTokenRepository;
import com.example.securecustomerapi.repository.UserRepository;
import com.example.securecustomerapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = tokenProvider.generateToken(authentication);

            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Delete existing refresh tokens for this user
            refreshTokenRepository.deleteByUser(user);

            // Generate refresh token (7 days expiry)
            String refreshTokenString = UUID.randomUUID().toString();
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(refreshTokenString);
            refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
            refreshTokenRepository.save(refreshToken);

            return new LoginResponseDTO(
                    token,
                    refreshTokenString,
                    "Bearer",
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name());
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER); // Default role
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToDTO(user);
    }

    @Override
    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        // Get current user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Check if new password matches confirmation
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }

        // Check if new password is different from current
        if (changePasswordDTO.getCurrentPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        // Hash and update password
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        // Find user by email
        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();

        // Set token and expiry (1 hour from now)
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        // In real application, send email with reset link
        // For now, return the token directly
        return resetToken;
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // Find user by reset token
        User user = userRepository.findByResetToken(resetPasswordDTO.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new InvalidPasswordException("Reset token has expired");
        }

        // Check if new password matches confirmation
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new InvalidPasswordException("Password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));

        // Clear reset token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    @Override
    public UserResponseDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToDTO(user);
    }

    @Override
    public UserResponseDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if email is being changed and if it's already taken by another user
        if (!user.getEmail().equals(updateProfileDTO.getEmail())) {
            if (userRepository.existsByEmail(updateProfileDTO.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
        }

        // Update profile
        user.setFullName(updateProfileDTO.getFullName());
        user.setEmail(updateProfileDTO.getEmail());

        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Password is incorrect");
        }

        // Soft delete - set isActive to false
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO updateRoleDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(updateRoleDTO.getRole());
        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    @Override
    public LoginResponseDTO refreshToken(String refreshTokenString) {
        // Find refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));

        // Check if token is expired
        if (LocalDateTime.now().isAfter(refreshToken.getExpiryDate())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidPasswordException("Refresh token has expired");
        }

        // Get user from refresh token
        User user = refreshToken.getUser();

        // Load UserDetails and generate new access token
        org.springframework.security.core.userdetails.UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        String newAccessToken = tokenProvider.generateToken(authentication);

        // Generate new refresh token
        String newRefreshTokenString = UUID.randomUUID().toString();
        refreshToken.setToken(newRefreshTokenString);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        return new LoginResponseDTO(
                newAccessToken,
                newRefreshTokenString,
                "Bearer",
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
