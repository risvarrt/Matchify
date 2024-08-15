package com.matchify.service.implementation;

import com.matchify.dto.response.FetchConnectedUsersResponse;
import com.matchify.dto.response.FindMatchesResponse;
import com.matchify.exception.UserNotFoundException;
import com.matchify.model.ChatStatusForUser;
import com.matchify.model.User;
import com.matchify.model.UserInterest;
import com.matchify.model.UserMatches;
import com.matchify.repository.UserInterestRepository;
import com.matchify.repository.UserMatchesRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MatchServiceImplTest {

  @Mock
  private UserInterestRepository userInterestRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthService authService;

  @Mock
  private UserMatchesRepository userMatchesRepository;

  @InjectMocks
  private MatchServiceImpl matchService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findMatches_UserNotFound_ThrowsException() {
    // Arrange
    when(authService.getUserIdFromToken()).thenReturn(null);
    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> matchService.findMatches());
  }

  @Test
  void findMatchingUsers_ReturnsMatchingUsers() {
    // Arrange
    User currentUser= createUserWithId(1,"Bob","Marley");

    List<UserInterest> allUserInterests = new ArrayList<>();
    UserInterest userInterest1 = createUserInterestWithId(2,1);
    UserInterest userInterest2 = createUserInterestWithId(3,1);

    allUserInterests.add(userInterest1);
    allUserInterests.add(userInterest2);

    when(userInterestRepository.findAll()).thenReturn(allUserInterests);

    // Act
    Map<Integer, Integer> matchingUsers = matchService.findMatchingUsers(List.of(1), currentUser.getUserId());

    // Assert
    assertNotNull(matchingUsers);
    assertEquals(2, matchingUsers.size());
    assertTrue(matchingUsers.containsKey(2));
    assertTrue(matchingUsers.containsKey(3));
    assertEquals(1, matchingUsers.get(2));
    assertEquals(1, matchingUsers.get(3));
  }

  @Test
  void getMatchedUsersProfile_ReturnsUserProfile() {
    // Arrange
    Map<Integer, Integer> topUserIds = new HashMap<>();
    topUserIds.put(2, 2);
    topUserIds.put(3, 1);

    User user2 = createUserWithId(2,"Rishi","Varman");
    User user3 = createUserWithId(3,"Mrunal","Patkar");

    when(userRepository.findById(2)).thenReturn(Optional.of(user2));
    when(userRepository.findById(3)).thenReturn(Optional.of(user3));

    // Act
    FindMatchesResponse response = matchService.getMatchedUsersProfile(topUserIds);

    // Assert
    assertNotNull(response);
    assertEquals(2, response.getMatchedusers().size());
    assertEquals("Rishi Varman", response.getMatchedusers().get(0).getName());
    assertEquals("Mrunal Patkar", response.getMatchedusers().get(1).getName());
  }

  @Test
  void matchExists_ReturnsTrueIfMatchExists() {
    // Arrange
    when(userMatchesRepository.existsByUser_UserIdAndMatchedUser_UserId(1, 2)).thenReturn(true);

    // Act
    boolean matchExists = matchService.matchExists(1, 2);

    // Assert
    assertTrue(matchExists);
  }

  @Test
  void storeMatch_StoresMatchInRepository() {
    // Arrange
    User user1 = createUserWithId(1,"Bob","Marley");
    User user2 = createUserWithId(2,"Rishi","Varman");

    when(userRepository.findById(1)).thenReturn(Optional.of(user1));
    when(userRepository.findById(2)).thenReturn(Optional.of(user2));

    // Act
    matchService.storeMatch(1, 2);

    // Assert
    verify(userMatchesRepository, times(1)).save(any(UserMatches.class));
  }

  private User createUserWithId(int userId, String firstName, String lastName) {
    return User.builder()
        .userId(userId)
        .firstName(firstName)
        .lastName(lastName)
        .build();
  }

  private UserInterest createUserInterestWithId(int userId, int interestId) {
    return UserInterest.builder()
        .userId(userId)
        .interestId(interestId)
        .build();
  }
}
