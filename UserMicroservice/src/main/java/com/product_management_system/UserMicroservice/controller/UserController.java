package com.product_management_system.UserMicroservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product_management_system.UserMicroservice.dto.LoginDTO;
import com.product_management_system.UserMicroservice.dto.LoginResponseDTO;
import com.product_management_system.UserMicroservice.dto.RegisterDTO;
import com.product_management_system.UserMicroservice.dto.RegisterResponseDTO;
import com.product_management_system.UserMicroservice.service.JwtService;
import com.product_management_system.UserMicroservice.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterDTO registerDTO) { 
        RegisterResponseDTO responseDTO = userService.register(registerDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody LoginDTO loginDTO, HttpServletResponse response) { 
        LoginResponseDTO loginResponseDTO = userService.login(loginDTO);
        String token = jwtService.createToken(loginResponseDTO.getEmail(), loginResponseDTO.getRole());
        response.addCookie(jwtService.createJwtCookie(token, 10803));
        loginResponseDTO.setJwtToken(token);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @GetMapping("/test")
    public String getMethodName() {
        return new String("Hello");
    }
    
    
}