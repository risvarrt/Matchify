package com.matchify.service.implementation;

import com.matchify.dto.UserWithoutPassword;
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
import com.matchify.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final EventAttendeesRepository eventAttendeesRepository;

  private final ChatRoomRepository chatRoomRepository;
  private final AuthService authService;
  private final ModelMapper modelMapper;

  /**
   * Connect the user
   *
   * @param userFromWs the user to connect
   * @return the connected user
   */
  public FetchConnectedUsersResponse connect(ConnectUser userFromWs) {
    System.out.println("User from ws: " + userFromWs.getUserId());
    User userFromDb = userRepository.findByUserId(Integer.parseInt(userFromWs.getUserId()));

    if (userFromDb == null) {
      throw new UserNotFoundException("User not found");
    }

    userFromDb.setStatus(ChatStatusForUser.ONLINE);
    userRepository.save(userFromDb);

    return buildConnectedUserResponse(userFromDb);
  }

  /**
   * Build the connected user response
   *
   * @param user the user to connect
   * @return the connected user
   */
  private FetchConnectedUsersResponse buildConnectedUserResponse(User user) {
    return modelMapper.map(user, FetchConnectedUsersResponse.class);
  }

  /**
   * Disconnect the user
   *
   * @param user the user to disconnect
   * @return the connected user
   */
  public FetchConnectedUsersResponse disconnect(ConnectUser user) {
    User userFromDb = userRepository.findByUserId(Integer.parseInt(user.getUserId()));

    if (userFromDb == null) {
      throw new UserNotFoundException("User not found");
    }

    userFromDb.setStatus(ChatStatusForUser.OFFLINE);
    userRepository.save(userFromDb);

    return buildConnectedUserResponse(userFromDb);
  }

  /**
   * Find connected users
   *
   * @return the connected user
   */
  public List<FetchConnectedUsersResponse> findConnectedUsers() {
    List<User> onlineUsers = userRepository.findAllByStatus(ChatStatusForUser.ONLINE);
    return onlineUsers.stream().map(this::buildConnectedUserResponse).toList();
  }

  /**
   * Fetch the logged in user details
   *
   * @return the user profile response and joined events
   */
  @Override
  public UserProfileResponse fetchLoggedInUserDetails() {
    Integer userId = authService.getUserIdFromToken();
    User user = userRepository.findByUserId(userId);
    List<Event> joinedEventsFromDb = eventAttendeesRepository.fetchJoinedEvents(userId);
    List<GetMyEventsResponse> joinedEvents = mapToGetMyEventsResponses(joinedEventsFromDb);

    return createUserProfileResponse(user, joinedEvents);
  }

  /**
   * Map the events to get my events responses
   *
   * @param events the events
   * @return the get my events responses
   */
  private List<GetMyEventsResponse> mapToGetMyEventsResponses(List<Event> events) {
    return events.stream()
        .map(
            event -> {
              GetMyEventsResponse eventResponse = modelMapper.map(event, GetMyEventsResponse.class);
              eventResponse.setCreatedBy(
                  modelMapper.map(event.getUser(), UserWithoutPassword.class));
              return eventResponse;
            })
        .toList();
  }

  /**
   * Create a user profile response
   *
   * @param user the user
   * @param joinedEvents the joined events
   * @return the user profile response
   */
  private UserProfileResponse createUserProfileResponse(
      User user, List<GetMyEventsResponse> joinedEvents) {
    UserProfileResponse userProfileResponse = new UserProfileResponse();
    userProfileResponse.setUserId(String.valueOf(user.getUserId()));
    userProfileResponse.setFirstName(user.getFirstName());
    userProfileResponse.setLastName(user.getLastName());
    userProfileResponse.setEmail(user.getEmail());
    userProfileResponse.setLocation(user.getLocation());
    userProfileResponse.setAgeRange(user.getAgeRange());
    userProfileResponse.setJoinedEvents(joinedEvents);
    return userProfileResponse;
  }

  /**
   * Find connected users by user ids
   *
   * @param userIds the user ids
   * @return the connected user
   */
  public List<UserChatStatusResponse> getStatusForUsers(List<Integer> userIds) {
    List<User> users = userRepository.findAllByUserIdIn(userIds);
    return users.stream().map(this::buildUserChatStatusResponse).toList();
  }

  /**
   * Build the user chat status response
   *
   * @return List of UserChatStatusResponse objects representing the status of all users.
   */
  private UserChatStatusResponse buildUserChatStatusResponse(User user) {
    return UserChatStatusResponse.builder()
        .userId(user.getUserId())
        .fullName(user.getFirstName() + " " + user.getLastName())
        .status(user.getStatus())
        .build();
  }

  /**
   * Retrieves the status of users who have been messaged by the current user.
   *
   * @return List of UserChatStatusResponse objects representing the status of messaged users.
   */
  @Override
  public List<UserChatStatusResponse> getMessagedUsers() {
    String senderId =
        userRepository.findByUserId(authService.getUserIdFromToken()).getUserId().toString();
    List<ChatRoom> chatrooms = chatRoomRepository.findBySenderId(senderId);
    List<Integer> messagedUserIds = mapChatroomsToUserIds(chatrooms);
    return getStatusForUsers(messagedUserIds);
  }

  /**
   * Maps chatrooms to user ids.
   *
   * @param chatrooms the chatrooms to map
   * @return List of user ids.
   */
  private List<Integer> mapChatroomsToUserIds(List<ChatRoom> chatrooms) {
    return chatrooms.stream()
        .map(chat -> Integer.parseInt(chat.getRecipientId()))
        .collect(Collectors.toList());
  }
}
