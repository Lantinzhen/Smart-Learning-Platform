package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/test/generate-password")
    public String generatePassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}