package com.product_management_system.UserMicroservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_management_system.UserMicroservice.model.User;


public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

}
