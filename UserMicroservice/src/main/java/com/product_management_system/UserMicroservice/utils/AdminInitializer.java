package com.product_management_system.UserMicroservice.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.product_management_system.UserMicroservice.model.Role;
import com.product_management_system.UserMicroservice.model.User;
import com.product_management_system.UserMicroservice.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Value("${default_email:admin@default.com}")
    private String defaultAdminEmail;

    @Value("${default_password:SuperSecretPassword}")
    private String defaultAdminPassword;

    

    @Override
    public void run(String... args) {
        System.out.println(defaultAdminPassword);

        if (!userRepository.existsByEmail(defaultAdminEmail)) {
            User admin = new User(
                defaultAdminEmail,
                "Admin",
                BCrypt.hashpw(defaultAdminPassword, BCrypt.gensalt()),
                Role.ADMIN
            );
            userRepository.save(admin);
        }
    }
}
