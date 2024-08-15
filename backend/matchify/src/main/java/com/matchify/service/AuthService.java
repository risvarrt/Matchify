package com.matchify.service;

import com.matchify.dto.response.SignUpResponse;
import com.matchify.model.User;
import com.matchify.dto.request.LoginRequest;
import com.matchify.dto.response.AuthResponse;

/**
 * Interface for authentication services.
 * Declares methods for adding users and authenticating them to get a JWT token.
 */
public interface AuthService {

     SignUpResponse addUser(User user);

     AuthResponse loginAndGetToken(LoginRequest loginRequest);

     Integer getUserIdFromToken();

}
