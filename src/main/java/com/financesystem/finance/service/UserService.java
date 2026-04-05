package com.financesystem.finance.service;

import com.financesystem.finance.dto.SafeUserDTO;
import com.financesystem.finance.dto.UserDTO;

import java.util.List;

public interface UserService {

    String login(String email, String password);

    void validatePassword(String password);

    void updateProfile(Long userId, String name, String email);

    void changePassword(Long userId, String oldPassword, String newPassword);

    void deleteUser(Long id);

    void createUserFromDTO(UserDTO userDTO);

    void logout(Long userId);

    List<SafeUserDTO> getAllSafeUsers();

    SafeUserDTO getSafeUserByEmail(String email);
}