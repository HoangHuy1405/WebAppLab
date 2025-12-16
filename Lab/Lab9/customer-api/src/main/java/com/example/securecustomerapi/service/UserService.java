package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.ChangePasswordDTO;
import com.example.securecustomerapi.dto.ForgotPasswordDTO;
import com.example.securecustomerapi.dto.LoginRequestDTO;
import com.example.securecustomerapi.dto.LoginResponseDTO;
import com.example.securecustomerapi.dto.RefreshTokenDTO;
import com.example.securecustomerapi.dto.RegisterRequestDTO;
import com.example.securecustomerapi.dto.ResetPasswordDTO;
import com.example.securecustomerapi.dto.UpdateProfileDTO;
import com.example.securecustomerapi.dto.UpdateRoleDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    LoginResponseDTO login(LoginRequestDTO loginRequest);

    UserResponseDTO register(RegisterRequestDTO registerRequest);

    UserResponseDTO getCurrentUser(String username);

    void changePassword(String username, ChangePasswordDTO changePasswordDTO);

    String forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    UserResponseDTO getProfile(String username);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO);

    void deleteAccount(String username, String password);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long id, UpdateRoleDTO updateRoleDTO);

    UserResponseDTO toggleUserStatus(Long id);

    LoginResponseDTO refreshToken(String refreshToken);
}
