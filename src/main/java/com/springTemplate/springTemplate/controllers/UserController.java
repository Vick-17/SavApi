package com.springTemplate.springTemplate.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springTemplate.springTemplate.models.User;
import com.springTemplate.springTemplate.models.Role;
import com.springTemplate.springTemplate.repositories.RoleRepository;
import com.springTemplate.springTemplate.repositories.UserRepository;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping(value = "/inscription", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public User createAdmin(@RequestBody User users) {
        User existingUser = userRepository.findByUsername(users.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("L'adresse e-mail est déjà utilisée.");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = bCryptPasswordEncoder.encode(users.getPassword());
        users.setPassword(passwordEncode);
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Role introuvable");
        }
        users.getRoles().add(userRole);
        users.setPoints(100);
        return userRepository.save(users);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
