 package com.product_management_system.Gateway.dtos;

 public class UserJwtDTO {
    private String id;
    private String role;

    public UserJwtDTO(String id, String role) {
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}