package com.matchify.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matchify.dto.request.FillInterestRequest;
import com.matchify.dto.response.SignUpResponse;
import com.matchify.model.User;
import com.matchify.service.AuthService;
import com.matchify.service.EventService;
import com.matchify.utils.TestUtil;
import java.util.List;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
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
class InterestControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private AuthService authService;

  @Autowired private EventService eventService;

  @BeforeEach
  public void setUp() {
    truncateTable("users");
    truncateTable("user_interests");
  }

  private void truncateTable(String tableName) {
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
    jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
  }

  @Test
  public void userGetsInterests() throws Exception {
    // Register a user
    User user = createUser();
    String token = registerUserAndGetToken(user);

    mockMvc
        .perform(
            get("/api/v1/interest")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()) // Check if response is an array
        .andExpect(
            jsonPath("$", Matchers.hasSize(Matchers.greaterThan(0)))) // Check if array is non-empty
        .andExpect(jsonPath("$[0].groupId").exists()) // Example validation of first element
        .andExpect(jsonPath("$[0].groupName").exists()) // Example validation of first element
        .andExpect(
            jsonPath("$[*].categories").isArray()) // Check if each group has a categories array
        .andExpect(
            jsonPath(
                "$[*].categories",
                Matchers.everyItem(
                    Matchers.hasSize(
                        Matchers.greaterThan(0))))); // Check if every categories array is non-empty
  }

  @Test
  public void userAddsInterests() throws Exception {
    // Register a user
    User user = createUser();
    String token = registerUserAndGetToken(user);

    FillInterestRequest fillInterestRequest = getFillInterestRequest();

    mockMvc
        .perform(
            post("/api/v1/interest/fill-my-interest")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(fillInterestRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.msg").value("Interest saved successfully"));
  }

  @NotNull
  private static FillInterestRequest getFillInterestRequest() {
    int sportsGroupId = 1;
    int academicGroupId = 2;
    int languageGroupId = 3;

    int badmintonSportsCategoryId = 1;
    int computerScienceAcademicCategoryId = 2;
    int englishLanguageCategoryId = 3;

    FillInterestRequest fillInterestRequest = new FillInterestRequest();

    fillInterestRequest.setCategories(List.of(sportsGroupId, academicGroupId, languageGroupId));
    fillInterestRequest.setGroups(
        List.of(
            badmintonSportsCategoryId,
            computerScienceAcademicCategoryId,
            englishLanguageCategoryId));
    return fillInterestRequest;
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

  // Helper method to convert object to JSON string
  private String asJsonString(final Object obj) throws Exception {
    return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
  }
}
