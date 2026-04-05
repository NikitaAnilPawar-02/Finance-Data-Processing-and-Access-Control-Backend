package com.financesystem.finance.controller;

import com.financesystem.finance.dto.SafeUserDTO;
import com.financesystem.finance.dto.UserDTO;
import com.financesystem.finance.entity.User;
import com.financesystem.finance.security.TokenValidator;
import com.financesystem.finance.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenValidator tokenValidator;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = userService.login(email, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        userService.createUserFromDTO(userDTO);
        return new ResponseEntity<>("User Created", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SafeUserDTO>> getAllUsers(HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        List<SafeUserDTO> safeUsers = userService.getAllSafeUsers();
        return new ResponseEntity<>(safeUsers, HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<SafeUserDTO> getUserByEmail(@RequestParam String email, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        SafeUserDTO safeUser = userService.getSafeUserByEmail(email);
        if (safeUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(safeUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        tokenValidator.checkAdmin(user);
        userService.deleteUser(id);
        return new ResponseEntity<>("User Deleted", HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestParam String name, @RequestParam String email, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        userService.updateProfile(user.getId(), name, email);
        return new ResponseEntity<>("Profile Updated", HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        userService.changePassword(user.getId(), oldPassword, newPassword);
        return new ResponseEntity<>("Password Changed", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        User user = tokenValidator.validate(request);
        userService.logout(user.getId());
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }
}