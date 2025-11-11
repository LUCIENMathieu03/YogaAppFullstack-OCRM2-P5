package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional  // pour rollback automatique des modifications en base
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void authenticateUser_shouldReturnJwtResponse_whenValidCredentials() throws Exception {
        // GIVEN : un utilisateur enregistré en base
        String email = "test@example.com";
        String rawPassword = "testPass123";
        User user = new User(email, "Last", "First", passwordEncoder.encode(rawPassword), true);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(rawPassword);

        // WHEN + THEN : on fait la requête POST /api/auth/login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.firstName").value("First"))
                .andExpect(jsonPath("$.lastName").value("Last"))
                .andExpect(jsonPath("$.admin").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void authenticateUser_shouldReturnUnauthorized_whenInvalidPassword() throws Exception {
        // GIVEN
        String email = "test@example.com";
        String correctPassword = "testPass123";
        User user = new User(email, "Last", "First", passwordEncoder.encode(correctPassword), true);
        userRepository.save(user);

        // Mauvais mot de passe
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("wrongPassword");

        // WHEN + THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void authenticateUser_shouldReturnUnauthorized_whenUnkonwEmail() throws Exception {
        // GIVEN
        String email = "test@example.com";
        String correctPassword = "testPass123";
        User user = new User(email, "Last", "First", passwordEncoder.encode(correctPassword), true);
        userRepository.save(user);

        // Mauvais mot de passe
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@email.com");
        loginRequest.setPassword(correctPassword);

        // WHEN + THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registerUser_shouldRegisterUser_whenNotAlreadyInDataBase() throws Exception {
        //GIVEN
        String email = "test@example.com";
        String userPassword = "testPass123";
        String firstName = "First";
        String lastName = "Last";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        signupRequest.setPassword(userPassword);
        signupRequest.setFirstName(firstName);
        signupRequest.setLastName(lastName);

        //WHEN - THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void registerUser_shouldNotRegisterUser_whenAlreadyInDataBase() throws Exception {
        // GIVEN : un utilisateur enregistré en base
        String email = "test@example.com";
        String rawPassword = "testPass123";
        User user = new User(email, "Last", "First", passwordEncoder.encode(rawPassword), true);
        userRepository.save(user);

        //Les même infos que le User en base
        String email2 = "test@example.com";
        String userPassword = "testPass123";
        String firstName = "First";
        String lastName = "Last";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email2);
        signupRequest.setPassword(userPassword);
        signupRequest.setFirstName(firstName);
        signupRequest.setLastName(lastName);

        //WHEN - THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

}
