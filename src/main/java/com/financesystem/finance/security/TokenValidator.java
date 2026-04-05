package com.financesystem.finance.security;

import com.financesystem.finance.entity.User;
import com.financesystem.finance.entity.Role;
import com.financesystem.finance.repository.UserRepository;
import com.financesystem.finance.exception.UnauthorizedException;
import com.financesystem.finance.exception.ForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Component
public class TokenValidator {

    @Autowired
    private UserRepository userRepository;

    public User validate(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            throw new UnauthorizedException("Token missing");
        }
        Optional<User> optionalUser = userRepository.findByToken(token);
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException("Invalid token");
        }
        User user = optionalUser.get();
        if (!user.isActive()) {
            throw new UnauthorizedException("User is inactive. Contact admin.");
        }
        return user;
    }

    public void checkAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only ADMIN allowed");
        }
    }

    public void checkAnalystOrAdmin(User user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.ANALYST) {
            throw new ForbiddenException("Only ANALYST or ADMIN allowed");
        }
    }
}