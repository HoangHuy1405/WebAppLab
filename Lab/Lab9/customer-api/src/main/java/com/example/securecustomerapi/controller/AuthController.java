package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response) {
        LoginResponseDTO loginResponse = userService.login(loginRequest);

        // Set refresh token in HTTP-only cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days in seconds
        // refreshTokenCookie.setSecure(true); // Enable in production with HTTPS
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // Clear refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge(0); // Delete cookie
        response.addCookie(refreshTokenCookie);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Logged out successfully. Token and cookie cleared.");
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.changePassword(username, dto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        String resetToken = userService.forgotPassword(dto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset token generated successfully");
        response.put("resetToken", resetToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = null;

        // Get refresh token from cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found in cookie");
        }

        LoginResponseDTO loginResponse = userService.refreshToken(refreshToken);

        // Set new refresh token in cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days in seconds
        // refreshTokenCookie.setSecure(true); // Enable in production with HTTPS
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(loginResponse);
    }

}
