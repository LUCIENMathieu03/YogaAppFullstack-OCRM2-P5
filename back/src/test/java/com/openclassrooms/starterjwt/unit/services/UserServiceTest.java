package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void delete_shouldCallRepositoryDeleteById(){
        //GIVEN
        Long id = 1234L;
        //WHEN
        userService.delete(id);
        //THEN
        verify(userRepository).deleteById(id);
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        //GIVEN
        Long id = 1234L;
        User user = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        //WHEN
        User result = userService.findById(id);
        //THEN
        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_shouldReturnNull_whenUserNotExists() {
        //GIVEN
        Long id = 1234L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //WHEN
        User result = userService.findById(id);
        //THEN
        assertThat(result).isNull();
    }

}
