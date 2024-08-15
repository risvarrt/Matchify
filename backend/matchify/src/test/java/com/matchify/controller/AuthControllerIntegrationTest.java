package com.matchify.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchify.dto.request.LoginRequest;
import com.matchify.model.User;
import com.matchify.service.AuthService;
import com.matchify.utils.TestUtil;
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
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        truncateTable("users");
    }
    private void truncateTable(String tableName) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
        jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
    }

    @Test
    public void testUserRegister() throws Exception {
        // Create a user registration request
        User user = createUser();

        // Perform the user registration request
        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));

        // Verify the response
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.message").value("User Registered Successfully"));
    }

    private static User createUser() {
        User user = new User();
        user.setFirstName("Vaibhav");
        user.setLastName("Singh");
        user.setEmail(TestUtil.generateUniqueEmail());
        user.setPassword("Password@123");
        user.setLocation("Halifax");
        user.setAgeRange("25-30");
        return user;
    }

    @Test
    public void testLogin() throws Exception {
        User user = createUser();

        // Perform the user registration request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(user.getPassword());


        // Perform the login request
        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)));

        // Verify the response
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.message").value("Login Success"));
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
