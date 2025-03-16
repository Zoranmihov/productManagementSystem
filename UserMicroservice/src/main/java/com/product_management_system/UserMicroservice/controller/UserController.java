package com.product_management_system.UserMicroservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/test")
    public String getMethodName() {
        return new String("Hello");
    }
    
    
}