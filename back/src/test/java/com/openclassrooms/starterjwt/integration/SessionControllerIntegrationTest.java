package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SessionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;


    @Test
    void shouldReturnUnauthorized_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findById_shouldReturnSession_whenSessionExists() throws Exception {
        // GIVEN : une session complète et valide
        Session session = Session.builder()
                .name("Yoga 1")
                .date(new Date())
                .description("Séance 1")
                .build();

        session = sessionService.create(session);

        // WHEN - THEN
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sessionMapper.toDto(session))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findById_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        // GIVEN : aucun session avec cet id
        Long nonExistentId = 999L;

        // WHEN -THEN
        mockMvc.perform(get("/api/session/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findById_shouldReturnBadRequest_whenIdIsNotAValidId() throws Exception {
        // WHEN : id non convertible en Long
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findAll_shouldReturnAllSessions() throws Exception {
        // GIVEN : plusieurs sessions sauvegardées
        Session session1 = Session.builder()
                .name("Yoga 1")
                .date(new Date())
                .description("Séance de Yoga 1")
                .build();

        Session session2 = Session.builder()
                .name("Yoga 2")
                .date(new Date())
                .description("Séance de Yoga 2")
                .build();

        session1 = sessionService.create(session1);
        session2 = sessionService.create(session2);

        List<Session> sessionList = List.of(session1, session2);

        // WHEN - THEN
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sessionMapper.toDto(sessionList))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void findAll_shouldReturnEmptyList_whenNoSessionsExist() throws Exception {
        // GIVEN : aucune session sauvegardée

        // WHEN + THEN
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));  // JSON vide
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void create_shouldReturnCreatedSession_whenValidInput() throws Exception {
        // GIVEN
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        teacher = teacherRepository.save(teacher);

        // Création et sauvegarde de user valides
        User user1 = User.builder()
                .email("user1@example.com")
                .firstName("Alice")
                .lastName("Martin")
                .password("encryptedPassword1")
                .admin(false)
                .build();
        user1 = userRepository.save(user1);

        SessionDto dto = new SessionDto();
        dto.setName("Séance Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("Une session de yoga");
        dto.setUsers(List.of(user1.getId()));

        // WHEN + THEN
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Séance Yoga"))
                .andExpect(jsonPath("$.description").value("Une session de yoga"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.users").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void create_shouldReturnBadRequest_whenInvalidSessionInput() throws Exception {
        // GIVEN
        SessionDto dto = new SessionDto();
        dto.setName("");
        dto.setDate(new Date());
        dto.setTeacher_id(1L);
        dto.setDescription("Description valide");
        dto.setUsers(List.of(1L, 2L));

        // WHEN + THEN : POST /api/session doit retourner 400 Bad Request
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void update_shouldReturnUpdatedSession_whenValidInput() throws Exception {
        // GIVEN
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        teacher = teacherRepository.save(teacher);

        User user1 = User.builder()
                .email("user1@example.com")
                .firstName("Alice")
                .lastName("Martin")
                .password("encryptedPassword1")
                .admin(false)
                .build();
        user1 = userRepository.save(user1);

        Session session = Session.builder()
                .name("Session Initiale")
                .date(new Date())
                .description("Description initiale")
                .build();
        session = sessionService.create(session);

        // Préparer un DTO avec des données modifiées
        SessionDto dto = new SessionDto();
        dto.setName("Session Modifiée");
        dto.setDate(new Date());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("Description modifiée");
        dto.setUsers(List.of(user1.getId()));

        // WHEN - THEN
        mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Session Modifiée"))
                .andExpect(jsonPath("$.description").value("Description modifiée"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void update_shouldReturnBadRequest_whenIdNotNumber() throws Exception {
        //GIVEN
        SessionDto dto = new SessionDto();
        dto.setName("Nom quelconque");
        dto.setDate(new Date());
        dto.setTeacher_id(1L);
        dto.setDescription("Description");

        //WHEN - THEN
        mockMvc.perform(put("/api/session/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void update_shouldReturnBadRequest_whenInvalidDto() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("");

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void delete_shouldReturnOk_whenSessionExists() throws Exception {
        // GIVEN
        Session session = Session.builder()
                .name("Séance à supprimer")
                .date(new Date())
                .description("Suppression test")
                .build();

        session = sessionService.create(session);

        // WHEN - THEN
        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void delete_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        // GIVEN
        Long nonExistentId = 99999L;

        // WHEN + THEN
        mockMvc.perform(delete("/api/session/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void delete_shouldReturnBadRequest_whenIdIsNotNumber() throws Exception {
        // WHEN + THEN
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void participate_shouldReturnOk_whenValidIds() throws Exception {
        //GIVEN
        Session session = Session.builder()
                .name("Yoga 1")
                .date(new Date())
                .description("Séance de Yoga 1")
                .users(new ArrayList<>())
                .build();
        session = sessionService.create(session);

        User user = User.builder()
                .email("user@example.com")
                .firstName("First")
                .lastName("Last")
                .password("password")
                .build();
        user = userRepository.save(user);

        //WHEN - THEN
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void participate_shouldReturnBadRequest_whenUserAlreadyParticipates() throws Exception {
        User user = User.builder()
                .email("user@example.com")
                .firstName("First")
                .lastName("Last")
                .password("password")
                .build();
        user = userRepository.save(user);

        Session session = Session.builder()
                .name("Yoga 1")
                .date(new Date())
                .description("Séance de Yoga 1")
                .users(new ArrayList<>(List.of(user)))
                .build();
        session = sessionService.create(session);

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void participate_shouldReturnNotFound_whenUserOrSessionMissing() throws Exception {
        mockMvc.perform(post("/api/session/9999/participate/8888"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void participate_shouldBadRequest_whenIdIsNotNumber() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/abc"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void noLongerParticipate_shouldReturnOk_whenUserIsParticipant() throws Exception {
        //GIVEN
        User user = User.builder()
                .email("user1@example.com")
                .firstName("Alice")
                .lastName("Martin")
                .password("encryptedPassword1")
                .admin(false)
                .build();
        user = userRepository.save(user);

        Session session = Session.builder()
                .name("Yoga 1")
                .date(new Date())
                .description("Séance Yoga 1")
                .users(new ArrayList<>(List.of(user)))
                .build();
        session = sessionService.create(session);

        //WHEN - THEN
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void noLongerParticipate_shouldReturnBadRequest_whenUserNotParticipant() throws Exception {
        //GIVEN
        User user = User.builder()
                .email("user2@example.com")
                .firstName("Bob")
                .lastName("Durand")
                .password("encryptedPassword2")
                .admin(false)
                .build();
        user = userRepository.save(user);

        Session session = Session.builder()
                .name("Yoga 2")
                .date(new Date())
                .description("Séance Yoga 2")
                .users(new ArrayList<>()) // aucun participant
                .build();
        session = sessionService.create(session);

        // WHEN - THEN
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void noLongerParticipate_shouldReturnNotFound_whenUserOrSessionMissing() throws Exception {
        mockMvc.perform(delete("/api/session/9999/participate/8888"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void noLongerParticipate_shouldReturnBadRequest_whenInvalidIds() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/xyz"))
                .andExpect(status().isBadRequest());
    }

}
