package com.product_management_system.UserMicroservice.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product_management_system.UserMicroservice.config.AppException;
import com.product_management_system.UserMicroservice.dto.LoginDTO;
import com.product_management_system.UserMicroservice.dto.LoginResponseDTO;
import com.product_management_system.UserMicroservice.dto.RegisterDTO;
import com.product_management_system.UserMicroservice.dto.RegisterResponseDTO;
import com.product_management_system.UserMicroservice.model.Role;
import com.product_management_system.UserMicroservice.model.User;
import com.product_management_system.UserMicroservice.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public RegisterResponseDTO register(RegisterDTO registerDTO) {

        // Create a new user entity
        Role role;
        if (registerDTO.getRole() == null || registerDTO.getRole().isEmpty()) {
            role = Role.CUSTOMER;
        } else {
            try {
                role = Role.valueOf(registerDTO.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException("Invalid role provided", HttpStatus.BAD_REQUEST);
            }
        }
        String hashedPassword = BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt());
        User user = new User(
                registerDTO.getEmail(),
                registerDTO.getName(),
                hashedPassword,
                role);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new AppException("Email is already in use", HttpStatus.BAD_REQUEST);
            }
            throw new AppException("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Prepare response
        return new RegisterResponseDTO("Welcome");
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        // Verify password
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Prepare and return response
        return new LoginResponseDTO("Login successful", user.getEmail(), user.getName(), user.getRole().toString(),
                null);
    }
}
