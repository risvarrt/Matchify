package com.matchify.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.response.*;
import com.matchify.model.EventAttendees;
import com.matchify.model.User;
import com.matchify.service.AuthService;
import com.matchify.service.EventService;
import com.matchify.utils.TestUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private AuthService authService;

  @Autowired private EventService eventService;

  @BeforeEach
  public void setUp() {
    truncateTable("users");
    truncateTable("events");
  }

  private void truncateTable(String tableName) {
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
    jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
  }

  /**
   * Test to verify that a user can create an event after registration 1. User registers 2. User
   * creates an event
   */
  @Test
  public void userCreatesAnEventAfterRegistration() throws Exception {
    // Register a user
    User user = createUser();
    String token = registerUserAndGetToken(user);

    CreateEventRequest eventRequest = createTestEvent();

    createEventUsingToken(eventRequest, token)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.eventId").exists())
        .andExpect(jsonPath("$.message").value("Event Created Successfully"));
  }

  @Test
  public void userShouldJoinToEvent() throws Exception {
    // Create users
    User user1 = createUser();
    User user2 = createUser();
    User user3 = createUser();

    // Register users and obtain tokens
    String token1 = registerUserAndGetToken(user1);
    String token2 = registerUserAndGetToken(user2);
    String token3 = registerUserAndGetToken(user3);

    // Create an event with user1's token
    CreateEventRequest eventRequest = createTestEvent();
    String response =
        createEventUsingToken(eventRequest, token1).andReturn().getResponse().getContentAsString();

    CreateEventResponse createEventResponse =
        new ObjectMapper().readValue(response, CreateEventResponse.class);
    int eventId = createEventResponse.getEventId();

    // Join event with user2
    mockMvc
        .perform(
            post("/api/v1/event/join-event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token2)
                .content(asJsonString(new EventAttendees(null, eventId, user2.getUserId()))))
        .andExpect(status().isOk())
        .andExpect(content().string("Event joined successfully."));

    // Join event with user3
    mockMvc
        .perform(
            post("/api/v1/event/join-event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token3)
                .content(asJsonString(new EventAttendees(null, eventId, user3.getUserId()))))
        .andExpect(status().isOk())
        .andExpect(content().string("Event joined successfully."));
  }

  @Test
  public void userShouldBeAbleToLeaveAJoinedEvent() throws Exception {
    // Create users
    User user1 = createUser();
    User user2 = createUser();
    User user3 = createUser();

    // Register users and obtain tokens
    String token1 = registerUserAndGetToken(user1);
    String token2 = registerUserAndGetToken(user2);
    String token3 = registerUserAndGetToken(user3);

    // Create an event with user1's token
    CreateEventRequest eventRequest = createTestEvent();
    String response =
        createEventUsingToken(eventRequest, token1).andReturn().getResponse().getContentAsString();

    CreateEventResponse createEventResponse =
        new ObjectMapper().readValue(response, CreateEventResponse.class);
    int eventId = createEventResponse.getEventId();

    // Join event with user2
    mockMvc
        .perform(
            post("/api/v1/event/join-event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token2)
                .content(asJsonString(new EventAttendees(null, eventId, user2.getUserId()))))
        .andExpect(status().isOk())
        .andExpect(content().string("Event joined successfully."));

    // Join event with user3
    mockMvc
        .perform(
            post("/api/v1/event/join-event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token3)
                .content(asJsonString(new EventAttendees(null, eventId, user3.getUserId()))))
        .andExpect(status().isOk())
        .andExpect(content().string("Event joined successfully."));

    // Leave event with user3
    mockMvc
            .perform(
                    post("/api/v1/event/leave-event")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token3)
                            .content(asJsonString(new EventAttendees(null, eventId, user3.getUserId()))))
            .andExpect(status().isOk())
            .andExpect(content().string("Event left successfully."));
}

    @NotNull
  private ResultActions createEventUsingToken(CreateEventRequest eventRequest, String token)
      throws Exception {
    // Create a multipart request with form data for the event
    return mockMvc.perform(
        multipart("/api/v1/event/create-event")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("eventName", eventRequest.getEventName())
            .param("description", eventRequest.getDescription())
            .param(
                "eventDate",
                eventRequest
                    .getEventDate()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .param(
                "startTime",
                eventRequest
                    .getStartTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
            .param(
                "endTime",
                eventRequest
                    .getEndTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
            .param("additionalInstructions", eventRequest.getAdditionalInstructions())
            .param("address", eventRequest.getAddress())
            .param("pincode", eventRequest.getPincode())
            .param("city", eventRequest.getCity())
            .header("Authorization", "Bearer " + token));
  }

  private String registerUserAndGetToken(User user) throws Exception {
    // Register user
    ResultActions resultActions =
        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
            .andExpect(status().isOk());

    // Extract token from response
    String token = resultActions.andReturn().getResponse().getContentAsString();
    SignUpResponse signUpResponse = new ObjectMapper().readValue(token, SignUpResponse.class);
    return signUpResponse.getToken();
  }

  // Helper method to create a user
  private User createUser() {
    User user = new User();
    user.setFirstName("Vaibhav");
    user.setLastName("Singh");
    user.setEmail(TestUtil.generateUniqueEmail());
    user.setPassword("Password@123");
    user.setLocation("Halifax");
    user.setAgeRange("25-30");
    return user;
  }

  private static CreateEventRequest createTestEvent() {
    LocalDate currentDate = LocalDate.now();
    CreateEventRequest eventRequest = new CreateEventRequest();
    eventRequest.setEventName("Test Event");
    eventRequest.setDescription("This is a test event");
    eventRequest.setEventDate(currentDate);
    eventRequest.setStartTime(LocalTime.of(10, 0));
    eventRequest.setEndTime(LocalTime.of(12, 0));
    eventRequest.setAdditionalInstructions("Please bring your own drinks");
    eventRequest.setAddress("6969 Bayers Road");
    eventRequest.setPincode("B3L 4P3");
    eventRequest.setCity("Halifax");
    return eventRequest;
  }

  // Helper method to convert object to JSON string
  private String asJsonString(final Object obj) throws Exception {
    return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
  }
}
