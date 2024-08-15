package com.matchify.controller;


import com.matchify.dto.response.SignUpResponse;
import com.matchify.model.User;
import com.matchify.dto.request.LoginRequest;
import com.matchify.dto.response.AuthResponse;
import com.matchify.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // PostMapping for handling POST requests for user login and token generation.
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginAndGetToken(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<AuthResponse>(authService.loginAndGetToken(loginRequest), HttpStatusCode.valueOf(200));

    }

    // PostMapping for handling POST requests for user registration.
    @PostMapping("/register")
    public ResponseEntity<SignUpResponse> addNewUser(@Valid @RequestBody User user) {
        return new ResponseEntity<SignUpResponse>(authService.addUser(user), HttpStatusCode.valueOf(200));
    }

}
