package com.matchify.service.implementation;

import com.matchify.config.JwtProvider;
import com.matchify.dto.request.LoginRequest;
import com.matchify.dto.response.AuthResponse;
import com.matchify.dto.response.SignUpResponse;
import com.matchify.exception.EmailAlreadyInUseException;
import com.matchify.model.ChatStatusForUser;
import com.matchify.model.User;
import com.matchify.repository.UserRepository;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

  @InjectMocks
  private AuthServiceImpl authServiceImpl;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() {
    reset(authenticationManager, jwtProvider, passwordEncoder, userRepository);
    SecurityContextHolder.clearContext();
  }

  @Test
  void testAddExistingUser() {
    // Arrange
    when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
    User user = createUser();

    // Act and Assert
    assertThrows(EmailAlreadyInUseException.class, () -> authServiceImpl.addUser(user));
    verify(userRepository).existsByEmail(eq("jane.doe@example.org"));
  }

  @Test
  void testAddNewUser() {
    // Arrange
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(jwtProvider.generateToken(Mockito.<String>any())).thenReturn("ABC123");
    User user = createUser();
    when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
    when(userRepository.save(Mockito.<User>any())).thenReturn(user);

    // Act
    SignUpResponse actualAddUserResult = authServiceImpl.addUser(user);

    // Assert
    verify(jwtProvider).generateToken(eq("jane.doe@example.org"));
    verify(userRepository).existsByEmail(eq("jane.doe@example.org"));
    verify(userRepository).save(Mockito.<User>any());
    verify(passwordEncoder).encode(Mockito.<CharSequence>any());
    assertEquals("ABC123", actualAddUserResult.getToken());
    assertEquals("User Registered Successfully", actualAddUserResult.getMessage());
    assertEquals("secret", user.getPassword());
  }

  @Test
  void getUserIdFromToken_UserFound_ReturnsUserId() {
    // Arrange
    User user = createUser();
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn(user.getEmail());
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    when(authServiceImpl.getUserEmailFromToken()).thenReturn("jane.doe@example.org");
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    // Act
    Integer userId = authServiceImpl.getUserIdFromToken();

    // Assert
    assertEquals(1, userId);
  }

  @Test
  void testLoginUserWithWrongCredentials() {
    // Arrange
    String userEmail = "nonexistent@example.com";
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    // Act & Assert
    assertThrows(NullPointerException.class, () -> authServiceImpl.getUserIdFromToken());
  }

  @Test
  void testGetUserEmailFromToken() {
    // Arrange
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("test@example.com");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Act
    String userEmail = authServiceImpl.getUserEmailFromToken();

    // Assert
    assertEquals("test@example.com", userEmail);
  }

  @Test
  void testLoginAndGetToken() {
    // Arrange
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");

    when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(createUser()));
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("test@example.com");

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(jwtProvider.generateToken(loginRequest.getEmail())).thenReturn("dummyToken");

    // Act
    AuthResponse authResponse = authServiceImpl.loginAndGetToken(loginRequest);

    // Assert
    assertNotNull(authResponse);
    assertEquals("dummyToken", authResponse.getJwt());
  }

  // Helper method to create a User object for testing
  private User createUser() {
    User user = new User();
    user.setAgeRange("Age Range");
    user.setEmail("jane.doe@example.org");
    user.setFirstName("Jane");
    user.setLastName("Doe");
    user.setLocation("Location");
    user.setPassword("password");
    user.setStatus(ChatStatusForUser.ONLINE);
    user.setUserId(1);
    return user;
  }
}
