package com.matchify.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matchify.dto.request.FillInterestRequest;
import com.matchify.dto.response.SignUpResponse;
import com.matchify.model.User;
import com.matchify.utils.TestUtil;
import java.util.List;
import org.hamcrest.Matchers;
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
class MatchControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  public void setUp() {
    truncateTable("users");
    truncateTable("user_interests");
  }

  private void truncateTable(String tableName) {
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
    jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
  }

  @BeforeEach
  public void setUpUser() throws Exception {
    // Create multiple users and fill interests for each user
    for (int i = 0; i < 4; i++) {
      User user = createUser();
      String token = registerUserAndGetToken(user);
      FillInterestRequest fillInterestRequest = getFillInterestRequest();
      mockMvc
          .perform(
              post("/api/v1/interest/fill-my-interest")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token)
                  .content(asJsonString(fillInterestRequest)))
          .andExpect(status().isCreated());
    }
  }

  @Test
  public void findMatches() throws Exception {
    // Choose one user to find matches
    User user = createUser();
    String token = registerUserAndGetToken(user);

    // User adds their interests
    FillInterestRequest fillInterestRequest = getFillInterestRequest();
    mockMvc
        .perform(
            post("/api/v1/interest/fill-my-interest")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(asJsonString(fillInterestRequest)))
        .andExpect(status().isCreated());

    // Send a GET request to the /api/v1/match/findMatches endpoint for the chosen user
    ResultActions resultActions =
        mockMvc.perform(
            get("/api/v1/match/findMatches")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Verify the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.matchedusers").isArray())
        .andExpect(
            jsonPath(
                "$.matchedusers",
                Matchers.hasSize(Matchers.greaterThan(0)))) // Check if array is non-empty
        .andExpect(
            jsonPath("$.matchedusers", Matchers.hasSize(4))); // size should be 4 as we 4 matches
  }

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

  private FillInterestRequest getFillInterestRequest() {
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

  private String asJsonString(final Object obj) throws Exception {
    return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
  }
}
