package com.example.ASM1.repository;

import com.example.ASM1.Entity.Product;
import com.example.ASM1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Integer> {
    User save(User user);

    Optional<User> findByUsername(String username);


    Optional<User> findByEmail(String email);
}