package com.matchify.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.matchify.dto.AddressCoordinates;
import com.matchify.dto.EventAddress;
import com.matchify.dto.EventImageName;
import com.matchify.dto.UserWithoutPassword;
import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.dto.response.GetMatchedEventsResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.exception.APINotAvailableException;
import com.matchify.exception.ImageUploadException;
import com.matchify.exception.UserNotFoundException;
import com.matchify.model.Event;
import com.matchify.model.EventAttendees;
import com.matchify.model.User;
import com.matchify.repository.EventAttendeesRepository;
import com.matchify.repository.EventRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class EventServiceImplTest {
  @InjectMocks
  private EventServiceImpl eventServiceImpl;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EventRepository eventRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private AmazonS3 s3Client;

  @Mock
  private AuthService authService;

  @Mock
  private EventAttendeesRepository eventAttendeesRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(eventServiceImpl, "bucketName", "testBucket");
  }

  @Test
  void testCreateEventEmptyFileException()
      throws IOException {
    // Arrange
    when(modelMapper.map(any(), any())).thenReturn(null);
    CreateEventRequest createEventRequest = new CreateEventRequest();
    MultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

    // Act and Assert
    assertThrows(ImageUploadException.class, () -> eventServiceImpl.createEvent(createEventRequest, file));
    verify(modelMapper, atLeast(1)).map(any(), any());
  }

  @Test
  void testCreateEventImageUploadException()
      throws IOException {
    // Arrange
    when(modelMapper.map(any(), any())).thenThrow(new ImageUploadException("An error occurred"));
    CreateEventRequest createEventRequest = new CreateEventRequest();
    MultipartFile multipartFile = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

    // Act and Assert
    assertThrows(ImageUploadException.class, () -> eventServiceImpl.createEvent(createEventRequest, multipartFile));
    verify(modelMapper).map(any(), any());
  }

  @Test
  void testStoreEvent() {
    // Arrange
    when(authService.getUserIdFromToken()).thenReturn(1);

    Event event = createDummyEvent(1);
    AddressCoordinates addressCoordinates = new AddressCoordinates("Latitude", "Longitude");
    String test_url = "https://example.org/example";
    when(eventRepository.save(any())).thenReturn(event);

    Event event2 = createDummyEvent(2);
    when(modelMapper.map(any(), any())).thenReturn(event2);

    User user3 = createDummyUser(3);
    when(userRepository.findByUserId(any())).thenReturn(user3);
    CreateEventRequest createEventRequest = new CreateEventRequest();

    // Act
    eventServiceImpl.storeEvent(createEventRequest, test_url, addressCoordinates);

    // Assert
    verify(userRepository).findByUserId(any());
    verify(authService).getUserIdFromToken();
    verify(modelMapper).map(any(), any());
    verify(eventRepository).save(any());
  }

  @Test
  void testStoreEvent_ImageUploadException() {
    // Arrange
    when(authService.getUserIdFromToken()).thenReturn(1);

    Event event = createDummyEvent(1);
    AddressCoordinates addressCoordinates = new AddressCoordinates("Latitude", "Longitude");
    String test_url = "https://example.org/example";

    when(modelMapper.map(any(), any())).thenReturn(event);
    when(userRepository.findByUserId(any())).thenThrow(new ImageUploadException("An error occurred"));
    CreateEventRequest createEventRequest = new CreateEventRequest();

    // Act and Assert
    assertThrows(ImageUploadException.class,
        () -> eventServiceImpl.storeEvent(createEventRequest, test_url, addressCoordinates));
    verify(userRepository).findByUserId(any());
    verify(authService).getUserIdFromToken();
    verify(modelMapper).map(any(), any());
  }

  @Test
  void testUploadInvalidFile()
      throws IOException {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

    // Act and Assert
    assertThrows(ImageUploadException.class,
        () -> eventServiceImpl.uploadFile(file, new EventImageName(1, "Eventname")));
  }

  @Test
  void testUploadFile()
      throws IOException {
    // Mocking dependencies
    MultipartFile file = new MockMultipartFile("test.png", "test.png", "image/png", "test".getBytes());
    EventImageName eventImageName = new EventImageName(1, "eventName");
    String test_url = "https://testBucket.s3.us-west-2.amazonaws.com/1/1_eventName_";

    // Mocking behavior
    when(s3Client.getRegionName()).thenReturn("us-west-2");
    when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(any(PutObjectResult.class));

    // Test
    String imageUrl = eventServiceImpl.uploadFile(file, eventImageName);

    // Verify
    assertEquals(test_url + System.currentTimeMillis() / 1000000 * 1000000 + ".png", imageUrl);
  }

  @Test
  void testGetCoordinates_APINotAvailableException() {
    EventAddress eventAddress = new EventAddress("42 Main St", "Oxford", "Pincode");
    // Arrange, Act and Assert
    assertThrows(APINotAvailableException.class, () -> eventServiceImpl.getCoordinates(eventAddress));
  }

  @Test
  public void testGetMatchedEvents() {
    // Mock user data
    int userId = 1;
    User loggedInUser = createDummyUser(userId);

    // Mock userRepository
    when(authService.getUserIdFromToken()).thenReturn(loggedInUser.getUserId());
    when(userRepository.findByUserId(loggedInUser.getUserId())).thenReturn(loggedInUser);
    when(userRepository.findAllByUserIdIn(anyList())).thenReturn(List.of(loggedInUser));

    // Mock event data
    Event event1 = createDummyEvent(1);
    Event event2 = createDummyEvent(2);
    Event event3 = createDummyEvent(3);

    event1.setUser(loggedInUser);
    event2.setUser(loggedInUser);
    event3.setUser(loggedInUser);

    // Mock eventRepository
    List<Event> events = new ArrayList<>();
    events.add(event1);
    events.add(event2);
    events.add(event3);
    when(eventRepository.findEventsForUsersByCitySortedByDate(anyList(), anyString(), anyInt(),
        any(Pageable.class))).thenReturn(events);
    when(eventRepository.findEventsForCity(anyString(), anyInt(), any(Pageable.class))).thenReturn(List.of(event1));

    // Mock modelMapper
    when(modelMapper.map(event1, GetMatchedEventsResponse.class)).thenReturn(new GetMatchedEventsResponse());
    when(modelMapper.map(event2, GetMatchedEventsResponse.class)).thenReturn(new GetMatchedEventsResponse());
    when(modelMapper.map(event3, GetMatchedEventsResponse.class)).thenReturn(new GetMatchedEventsResponse());
    when(modelMapper.map(loggedInUser, UserWithoutPassword.class)).thenReturn(new UserWithoutPassword());

    // Test the method
    List<GetMatchedEventsResponse> matchedEvents = eventServiceImpl.getMatchedEvents(List.of(loggedInUser.getUserId()));

    // Verify the results
    assertEquals(3, matchedEvents.size()); // Expecting 3 events as a result
  }

  @Test
  public void testGetMyEvents() {
    // Mock data
    Integer userId = 1;
    User user = new User();
    List<Event> events = new ArrayList<>();
    events.add(new Event());

    // Mock behavior
    when(authService.getUserIdFromToken()).thenReturn(userId);
    when(userRepository.findByUserId(userId)).thenReturn(user);
    when(eventRepository.findAllByUser(user)).thenReturn(events);
    when(modelMapper.map(any(Event.class), eq(GetMyEventsResponse.class))).thenReturn(new GetMyEventsResponse());
    when(modelMapper.map(any(User.class), eq(UserWithoutPassword.class))).thenReturn(new UserWithoutPassword());

    // Call the method
    List<GetMyEventsResponse> actualResponses = eventServiceImpl.getMyEvents();

    // Verify
    assertFalse(actualResponses.isEmpty());
  }

  @Test
  public void testGetMyEventsUserNotFound() {
    // Mock data
    Integer userId = 1;

    // Mock behavior
    when(authService.getUserIdFromToken()).thenReturn(userId);
    when(userRepository.findByUserId(userId)).thenReturn(null);

    // Call the method (which should throw UserNotFoundException)
    assertThrows(UserNotFoundException.class, () -> {
      eventServiceImpl.getMyEvents();
    });
  }

  private Event createDummyEvent(int id) {
    User user = createDummyUser(id);
    Event event = new Event();
    event.setEventId(id); // Dummy event ID
    event.setUser(user); // Dummy user
    event.setEventName("My Event");
    event.setDescription("Event Description");
    event.setEventDate(LocalDate.now());
    event.setStartTime(LocalTime.of(10, 0)); // Dummy start time
    event.setEndTime(LocalTime.of(12, 0)); // Dummy end time
    event.setAdditionalInstructions("Additional Instructions");
    event.setAddress("Address");
    event.setPincode("12345"); // Dummy pincode
    event.setCity("Halifax");
    event.setImageURL("dummy_image_url");
    event.setLatitude("dummy_latitude");
    event.setLongitude("dummy_longitude");
    return event;
  }

  private User createDummyUser(int id) {
    User user = new User();
    user.setUserId(id); // Dummy user ID
    user.setEmail(id + "@a.com");
    user.setAgeRange("18-25");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setLocation("Halifax");
    return user;
  }

  @Test
  void testJoinEvent() {
    EventAttendees eventAttendees = new EventAttendees(Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1));
    when(eventAttendeesRepository.findEventAttendees(anyInt(), anyInt())).thenReturn(null);

    String result = eventServiceImpl.joinEvent(eventAttendees);
    Assertions.assertEquals("Event joined successfully.", result);
  }

  @Test
  void testJoinEventAlreadyJoined() {
    EventAttendees eventAttendees = new EventAttendees(Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1));
    when(eventAttendeesRepository.findEventAttendees(anyInt(), anyInt())).thenReturn(eventAttendees);

    String result = eventServiceImpl.joinEvent(eventAttendees);
    Assertions.assertEquals("User has already joined the event.", result);
  }

  @Test
  void testLeaveEvent() {
    EventAttendees eventAttendees = new EventAttendees(Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1));
    String result = eventServiceImpl.leaveEvent(eventAttendees);
    verify(eventAttendeesRepository).deleteAttendees(anyInt(), anyInt());
    Assertions.assertEquals("Event left successfully.", result);
  }


  @Test
  void testFindAllEventAttendees() {
    EventAttendeesResponse eventAttendeesResponse = new EventAttendeesResponse(Integer.valueOf(1), "firstName", "lastName");
    when(eventAttendeesRepository.findEventAttendeesList(anyInt())).thenReturn(List.of(eventAttendeesResponse));

    List<EventAttendeesResponse> result = eventServiceImpl.findAllEventAttendees(new EventAttendeesListRequest(Integer.valueOf(1)));
    Assertions.assertEquals(List.of(eventAttendeesResponse), result);
  }

}
