package com.bas.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bas.authentication.dto.LoginRequest;
import com.bas.authentication.dto.RegisterRequest;
import com.bas.authentication.services.AuthService;

@RestController
@RequestMapping("/auth")

public class AuthController {

	@Autowired
    private  AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    	System.out.println("+++++++++++++++");
        return ResponseEntity.ok(authService.login(req));
    }
}

