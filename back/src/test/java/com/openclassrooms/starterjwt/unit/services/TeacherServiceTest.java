package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void findAll_shouldReturnAllTeachers() {
        //GIVEN
        Teacher teacher1 = new Teacher();
        Teacher teacher2 = new Teacher();
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        when(teacherRepository.findAll()).thenReturn(teachers);
        //WHEN
        List<Teacher> result = teacherService.findAll();
        //THEN
        assertThat(result).isEqualTo(teachers);
        verify(teacherRepository).findAll();
    }

    @Test
    void findById_shouldReturnTeacher_whenExists() {
        //GIVEN
        Long id = 1234L;
        Teacher teacher = new Teacher();

        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));
        //WHEN
        Teacher result = teacherService.findById(id);
        //THEN
        assertThat(result).isEqualTo(teacher);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        //GIVEN
        Long id = 1234L;
        when(teacherRepository.findById(id)).thenReturn(Optional.empty());
        //WHEN
        Teacher result = teacherService.findById(id);
        //THEN
        assertThat(result).isNull();
    }

}
