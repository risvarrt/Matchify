package com.matchify.controller;

import com.matchify.dto.request.LoginRequest;
import com.matchify.dto.response.AuthResponse;
import com.matchify.dto.response.SignUpResponse;
import com.matchify.model.ChatStatusForUser;
import com.matchify.model.User;
import com.matchify.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginAndGetToken() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("m.p@gmail.com", "Password@123");
        AuthResponse expectedResponse = new AuthResponse("dummyJwtTokenABC123", "Successful login");

        when(authService.loginAndGetToken(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> responseEntity = authController.loginAndGetToken(loginRequest);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("dummyJwtTokenABC123", responseEntity.getBody().getJwt());
        Assertions.assertEquals("Successful login", responseEntity.getBody().getMessage());
    }

    @Test
    public void testAddNewUser() {
        // Arrange (Dummy data)
        User newUser = new User(
                2,
                "Bob",
                "Builder",
                "Bob.b@example.com",
                "Password@456",
                "Gotham City",
                "20-30",
                ChatStatusForUser.ONLINE
        );

        SignUpResponse expectedResponse = new SignUpResponse("dummySignUpTokenXYZ789", "Registration successful");

        when(authService.addUser(any(User.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<SignUpResponse> responseEntity = authController.addNewUser(newUser);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("dummySignUpTokenXYZ789", responseEntity.getBody().getToken());
        Assertions.assertEquals("Registration successful", responseEntity.getBody().getMessage());
    }
}
