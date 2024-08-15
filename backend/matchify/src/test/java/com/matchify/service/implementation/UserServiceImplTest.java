package com.matchify.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.matchify.dto.response.FetchConnectedUsersResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.dto.response.UserChatStatusResponse;
import com.matchify.dto.response.UserProfileResponse;
import com.matchify.dto.ws.ConnectUser;
import com.matchify.exception.UserNotFoundException;
import com.matchify.model.ChatRoom;
import com.matchify.model.ChatStatusForUser;
import com.matchify.model.Event;
import com.matchify.model.User;
import com.matchify.repository.ChatRoomRepository;
import com.matchify.repository.EventAttendeesRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import static org.mockito.ArgumentMatchers.any;

class UserServiceImplTest {

  private UserServiceImpl userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private AuthService authService;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private EventAttendeesRepository eventAttendeesRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userRepository,  eventAttendeesRepository, chatRoomRepository, authService, modelMapper);
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, chatRoomRepository, authService, modelMapper, eventAttendeesRepository);
  }


  @Test
  void connect_userExists() {
    int userId = 1;
    // Arrange
    ConnectUser connectUser = ConnectUser.builder().userId(String.valueOf(userId)).build();
    User userFromDb = createUserWithId(userId, "John", "Doe", ChatStatusForUser.OFFLINE);

    when(userRepository.findByUserId(userId)).thenReturn(userFromDb);
    when(modelMapper.map(userFromDb, FetchConnectedUsersResponse.class)).thenReturn(
            createConnectedUserResponse(userFromDb, ChatStatusForUser.ONLINE));

    // Act
    FetchConnectedUsersResponse response = userService.connect(connectUser);

    // Assert
    assertResponseMatchesUser(response, userFromDb);
    verify(userRepository, times(1)).findByUserId(userId);
    verify(userRepository, times(1)).save(userFromDb);
    verify(modelMapper, times(1)).map(userFromDb, FetchConnectedUsersResponse.class);
  }

  @Test
  void connect_userNotFound() {
    int userId = 1;
    // Arrange
    ConnectUser connectUser = ConnectUser.builder().userId(String.valueOf(userId)).build();

    when(userRepository.findByUserId(userId)).thenReturn(null);

    // Assert
    assertThrows(UserNotFoundException.class, () -> userService.connect(connectUser));
    verify(userRepository, times(1)).findByUserId(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void disconnect_userExists() {
    int userId = 1;
    // Arrange
    ConnectUser connectUser = ConnectUser.builder().userId(String.valueOf(userId)).build();
    User userFromDb = createUserWithId(userId, "John", "Doe", ChatStatusForUser.ONLINE);

    when(userRepository.findByUserId(userId)).thenReturn(userFromDb);
    when(modelMapper.map(userFromDb, FetchConnectedUsersResponse.class)).thenReturn(
            createConnectedUserResponse(userFromDb, ChatStatusForUser.OFFLINE));

    // Act
    FetchConnectedUsersResponse response = userService.disconnect(connectUser);

    // Assert
    assertResponseMatchesUser(response, userFromDb);
    verify(userRepository, times(1)).findByUserId(userId);
    verify(userRepository, times(1)).save(userFromDb);
    verify(modelMapper, times(1)).map(userFromDb, FetchConnectedUsersResponse.class);
  }

  @Test
  void disconnect_userNotFound() {
    int userId = 1;
    // Arrange
    ConnectUser connectUser = ConnectUser.builder().userId(String.valueOf(userId)).build();

    when(userRepository.findByUserId(userId)).thenReturn(null);

    // Assert
    assertThrows(UserNotFoundException.class, () -> userService.disconnect(connectUser));
    verify(userRepository, times(1)).findByUserId(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void findConnectedUsers() {
    // Arrange
    int user1Id = 1;
    int user2Id = 2;
    User user1 = createUserWithId(user1Id, "John", "Doe", ChatStatusForUser.ONLINE);
    User user2 = createUserWithId(user2Id, "Jane", "Smith", ChatStatusForUser.ONLINE);
    List<User> connectedUsers = Arrays.asList(user1, user2);

    when(userRepository.findAllByStatus(ChatStatusForUser.ONLINE)).thenReturn(connectedUsers);

    when(modelMapper.map(user1, FetchConnectedUsersResponse.class)).thenReturn(
            createConnectedUserResponse(user1, user1.getStatus()));

    when(modelMapper.map(user2, FetchConnectedUsersResponse.class)).thenReturn(
            createConnectedUserResponse(user2, user2.getStatus()));

    // Act
    List<FetchConnectedUsersResponse> responses = userService.findConnectedUsers();

    // Assert
    assertEquals(connectedUsers.size(), responses.size());
    assertResponseMatchesUser(responses.get(0), user1);
    assertResponseMatchesUser(responses.get(1), user2);
    verify(userRepository, times(1)).findAllByStatus(ChatStatusForUser.ONLINE);
    verify(modelMapper, times(1)).map(user1, FetchConnectedUsersResponse.class);
    verify(modelMapper, times(1)).map(user2, FetchConnectedUsersResponse.class);
  }

  @Test
  void testGetStatusForUsers() {
    // Mock data
    int user1Id = 1;
    int user2Id = 2;
    List<Integer> userIds = Arrays.asList(user1Id, user2Id);
    User user1 = createUserWithId(user1Id, "John", "Doe", ChatStatusForUser.ONLINE);
    User user2 = createUserWithId(user2Id, "Alice", "Smith", ChatStatusForUser.OFFLINE);
    List<User> users = Arrays.asList(user1, user2);

    // Mock userRepository behavior
    when(userRepository.findAllByUserIdIn(userIds)).thenReturn(users);

    // Call the method under test
    List<UserChatStatusResponse> userChatStatusResponses = userService.getStatusForUsers(userIds);

    // Assertions
    assertEquals(users.size(), userChatStatusResponses.size());
    assertUserChatStatusResponseMatchesUser(userChatStatusResponses.get(0), user1);
    assertUserChatStatusResponseMatchesUser(userChatStatusResponses.get(1), user2);
  }

  @Test
  void testGetMessagedUsers() {
    // Mock data
    int user1Id = 1;
    int user2Id = 2;
    int user3Id = 3;
    User user = createUserWithId(user1Id, "John", "Doe", ChatStatusForUser.ONLINE);
    User user2 = createUserWithId(user2Id, "Brom", "Bones", ChatStatusForUser.ONLINE);
    User user3 = createUserWithId(user3Id, "Emily", "Shan", ChatStatusForUser.ONLINE);
    Long chatRoomId = 1L;
    Long chatRoomId2 = 2L;
    List<ChatRoom> chatRooms = Arrays.asList(
            new ChatRoom(chatRoomId, "1_2", "1", "2"),
            new ChatRoom(chatRoomId2, "1_3", "1", "3")
    );
    List<Integer> messagedUserIds = Arrays.asList(user2.getUserId(), user3.getUserId());
    List<User> messagedUsers = Arrays.asList(user2, user3);

    when(authService.getUserIdFromToken()).thenReturn(user1Id);
    when(userRepository.findByUserId(user1Id)).thenReturn(user);
    when(chatRoomRepository.findBySenderId(String.valueOf(user1Id))).thenReturn(chatRooms);
    when(userRepository.findAllByUserIdIn(messagedUserIds)).thenReturn(messagedUsers);

    List<UserChatStatusResponse> result = userService.getMessagedUsers();

    // Assertions
    assertEquals(messagedUsers.size(), result.size());
  }

  // Helper methods

  private User createUserWithId(int userId, String firstName, String lastName, ChatStatusForUser status) {
    return User.builder()
            .userId(userId)
            .firstName(firstName)
            .lastName(lastName)
            .status(status)
            .build();
  }

  private FetchConnectedUsersResponse createConnectedUserResponse(User user, ChatStatusForUser status) {
    return FetchConnectedUsersResponse.builder()
            .userId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .status(status)
            .build();
  }

  private void assertResponseMatchesUser(FetchConnectedUsersResponse response, User user) {
    assertEquals(user.getUserId(), response.getUserId());
    assertEquals(user.getFirstName(), response.getFirstName());
    assertEquals(user.getLastName(), response.getLastName());
    assertEquals(user.getStatus(), response.getStatus());
  }

  private UserChatStatusResponse createUserChatStatusResponse(User user) {
    return UserChatStatusResponse.builder()
            .userId(user.getUserId())
            .fullName(user.getFirstName() + user.getLastName())
            .status(user.getStatus())
            .build();
  }

  private void assertUserChatStatusResponseMatchesUser(UserChatStatusResponse response, User user) {
    assertEquals(user.getUserId(), response.getUserId());
    assertEquals(user.getStatus(), response.getStatus());
  }

  @Test
  void testFetchLoggedInUserDetails() {
    // Mock data
    Integer userId = 1;
    User user = createUser();
    List<Event> events = createEvents();

    // Mock dependencies
    when(authService.getUserIdFromToken()).thenReturn(userId);
    when(userRepository.findByUserId(userId)).thenReturn(user);
    when(eventAttendeesRepository.fetchJoinedEvents(userId)).thenReturn(events);
    when(modelMapper.map(any(), eq(GetMyEventsResponse.class))).thenReturn(new GetMyEventsResponse());

    // Call the method under test
    UserProfileResponse userProfileResponse = userService.fetchLoggedInUserDetails();

    // Verify the result
    assertEquals(String.valueOf(userId), userProfileResponse.getUserId());
    assertEquals(user.getFirstName(), userProfileResponse.getFirstName());
    assertEquals(user.getLastName(), userProfileResponse.getLastName());
    assertEquals(user.getEmail(), userProfileResponse.getEmail());
    assertEquals(user.getLocation(), userProfileResponse.getLocation());
    assertEquals(user.getAgeRange(), userProfileResponse.getAgeRange());
    assertEquals(events.size(), userProfileResponse.getJoinedEvents().size());

    // Verify that dependencies are called with the expected arguments
    verify(authService, times(1)).getUserIdFromToken();
    verify(userRepository, times(1)).findByUserId(userId);
    verify(eventAttendeesRepository, times(1)).fetchJoinedEvents(userId);
    verify(modelMapper, times(events.size())).map(any(), eq(GetMyEventsResponse.class));
  }

  // Helper methods to create mock data
  private User createUser() {
    User user = new User();
    user.setUserId(1);
    user.setFirstName("Vaibhav");
    user.setLastName("Singh");
    user.setEmail("vaibhav.singh@dal.ca");
    user.setLocation("Halifax");
    user.setAgeRange("25-30");
    return user;
  }

  private List<Event> createEvents() {
    Event event1 = new Event();
    event1.setEventId(1);
    event1.setEventName("Event 1");

    Event event2 = new Event();
    event2.setEventId(2);
    event2.setEventName("Event 2");

    return Arrays.asList(event1, event2);
  }
}
