package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TeacherControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TeacherMapper TeacherMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void shouldReturnUnauthorized_whenNoAuth() throws Exception {
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user")
    void findAll_shouldReturnListOfTeachers() throws Exception {
        Teacher teacher1 = Teacher.builder().lastName("Last1").firstName("First1").build();
        Teacher teacher2 = Teacher.builder().lastName("Last2").firstName("First2").build();
        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TeacherMapper.toDto(List.of(teacher1, teacher2)))));
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnAUser_whitAValidId() throws Exception {
        Teacher teacher = Teacher.builder()
                .lastName("Last")
                .firstName("First")
                .build();

        teacherRepository.save(teacher);

        mockMvc.perform(get("/api/teacher/" + teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TeacherMapper.toDto(teacher))));
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnNotFound_whenIdNotExist() throws Exception {
        mockMvc.perform(get("/api/teacher/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_shouldReturnBadRequest_whenInvalidId() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid-id"))
                .andExpect(status().isBadRequest());
    }


}
