package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.UpdateProfileDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;
import com.example.securecustomerapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponseDTO profile = userService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponseDTO updatedProfile = userService.updateProfile(username, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestParam String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.deleteAccount(username, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Account deactivated successfully");
        return ResponseEntity.ok(response);
    }
}
