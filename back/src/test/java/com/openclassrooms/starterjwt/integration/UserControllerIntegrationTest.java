package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnUnauthorized_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // GIVEN
        String nonexistentId = "123";

        // WHEN + THEN :
        mockMvc.perform(get("/api/user/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnBadRequest_whenIdInvalid() throws Exception {
        // GIVEN
        String invalidId = "abc";

        // WHEN + THEN
        mockMvc.perform(get("/api/user/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        // GIVEN
        User user = new User("email@test.com", "Last", "First", "encryptedPass", true);
        userRepository.save(user);

        // WHEN + THEN
        mockMvc.perform(get("/api/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userMapper.toDto(user))));
    }

    @Test
    @WithMockUser(username = "user")
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // GIVEN
        String nonexistentId = "123";

        // WHEN + THEN
        mockMvc.perform(delete("/api/user/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "email@test.com")
        // correspond à l'email de l'utilisateur
    void deleteUser_shouldDeleteUser_whenAuthenticatedUserIsSame() throws Exception {
        // GIVEN
        User user = new User("email@test.com", "Last", "First", "encryptedPass", true);
        userRepository.save(user);

        // WHEN + THEN
        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isOk());

        assert (userRepository.findById(user.getId()).isEmpty());
    }

    @Test
    @WithMockUser(username = "otheruser@test.com")
        // utilisateur différent
    void deleteUser_shouldReturnUnauthorized_whenAuthenticatedUserIsDifferent() throws Exception {
        // GIVEN
        User user = new User("email@test.com", "Last", "First", "encryptedPass", true);
        userRepository.save(user);

        // WHEN + THEN
        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

}
