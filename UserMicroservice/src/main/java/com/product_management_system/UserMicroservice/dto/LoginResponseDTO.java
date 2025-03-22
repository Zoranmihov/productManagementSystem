package com.product_management_system.UserMicroservice.dto;

public class LoginResponseDTO {
    private String message;
    private String email;
    private String name;
    private String role;
    private String JwtToken;


    public LoginResponseDTO(String message, String email, String name, String role, String jwtToken) {
        this.message = message;
        this.email = email;
        this.name = name;
        this.role = role;
        this.JwtToken = jwtToken;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public String getJwtToken() {
        return JwtToken;
    }


    public void setJwtToken(String jwtToken) {
        JwtToken = jwtToken;
    }


   




}
