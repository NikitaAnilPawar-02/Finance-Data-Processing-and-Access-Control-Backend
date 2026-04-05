package com.financesystem.finance.service;

import com.financesystem.finance.dto.SafeUserDTO;
import com.financesystem.finance.dto.UserDTO;
import com.financesystem.finance.entity.User;
import com.financesystem.finance.repository.UserRepository;
import com.financesystem.finance.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUserFromDTO(UserDTO dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        validatePassword(dto.getPassword());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(dto.isActive());
        user.setRole(dto.getRole());
        userRepository.save(user);
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UnauthorizedException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new ForbiddenException("User is inactive");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        String token = java.util.UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);
        return token;
    }

    @Override
    public void validatePassword(String password) {
        if (password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new BadRequestException("Password must contain at least one special character (!@#$%^&*())");
        }
    }

    @Override
    public void updateProfile(Long userId, String name, String email) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User other = existing.get();
            if (!other.getId().equals(userId)) {
                throw new BadRequestException("Email already in use");
            }
        }
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        if (!newPassword.matches(regex)) {
            throw new BadRequestException(
                    "New password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character"
            );
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.delete(optionalUser.get());
    }

    @Override
    public List<SafeUserDTO> getAllSafeUsers() {
        List<User> users = userRepository.findAll();
        List<SafeUserDTO> safeUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            SafeUserDTO safeUser = new SafeUserDTO(
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.isActive(),
                    u.getRole()
            );
            safeUsers.add(safeUser);
        }
        return safeUsers;
    }

    @Override
    public SafeUserDTO getSafeUserByEmail(String email) {
        User u = userRepository.findByEmail(email).orElse(null);
        if (u == null) {
            return null;
        }
        return new SafeUserDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.isActive(),
                u.getRole()
        );
    }

    @Override
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        user.setToken(null);
        userRepository.save(user);
    }
}