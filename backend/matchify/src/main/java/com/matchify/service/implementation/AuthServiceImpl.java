package com.matchify.service.implementation;

import com.matchify.config.JwtProvider;
import com.matchify.dto.response.SignUpResponse;
import com.matchify.exception.EmailAlreadyInUseException;
import com.matchify.exception.UserNotFoundException;
import com.matchify.exception.WrongCredentialsException;
import com.matchify.model.User;
import com.matchify.repository.UserRepository;
import com.matchify.dto.request.LoginRequest;
import com.matchify.dto.response.AuthResponse;
import com.matchify.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user with the provided user details.
     *
     * @param user The user to be added to the system.
     * @return SignUpResponse if registration is successful.
     * @throws EmailAlreadyInUseException If the email is already registered.
     */
    @Override
    public SignUpResponse addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException("Email already in use.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        SignUpResponse signUpResponse = new SignUpResponse();
        // generate a JWT token for the user
        signUpResponse.setToken(jwtProvider.generateToken(user.getEmail()));
        signUpResponse.setMessage("User Registered Successfully");
        return signUpResponse;
    }

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param loginRequest The login request with credentials.
     * @return AuthResponse with JWT token if authentication is successful.
     * @throws WrongCredentialsException If authentication fails.
     */
    @Override
    public AuthResponse loginAndGetToken(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new WrongCredentialsException("Invalid Email or Password");
        }
        try {
            // Attempt to authenticate the user with provided credentials
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
          authenticationManager.authenticate(authenticationToken);
          AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwtProvider.generateToken(email));
            authResponse.setMessage("Login Success");
            return authResponse;
        } catch (AuthenticationException e) {
            throw new WrongCredentialsException("Invalid Email or Password");
        }
    }

    /**
     * Get the user id from the JWT token.
     *
     * @return The user id if the user is found, null otherwise.
     */
    public Integer getUserIdFromToken() {
        // Get the user email from the JWT token
        String userEmail = getUserEmailFromToken();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user != null) {
            return user.getUserId();
        } else {
            // Handle the case where user is not found
            return null;
        }
    }

    /**
     * Get the user email from the JWT token.
     *
     * @return The user email if the user is found, null otherwise.
     */
    public String getUserEmailFromToken() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication().
                getPrincipal();
        return userDetails.getUsername();
    }

}
