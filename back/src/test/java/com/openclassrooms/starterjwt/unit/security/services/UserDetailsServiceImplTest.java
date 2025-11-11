package com.openclassrooms.starterjwt.unit.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUserName_shouldReturnUser_whenValidUserName() {
        //GIVEN
        String username = "exemple@username.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(username);
        user.setLastName("Last");
        user.setFirstName("First");
        user.setPassword("password");

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        //WHEN
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //THEN
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(((UserDetailsImpl) userDetails).getId()).isEqualTo(1L);
        assertThat(((UserDetailsImpl) userDetails).getFirstName()).isEqualTo("First");
        assertThat(((UserDetailsImpl) userDetails).getLastName()).isEqualTo("Last");
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserNotFound() {
        // GIVEN
        String username = "unknown@example.com";

        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // WHEN - THEN : la méthode doit lever une exception UsernameNotFoundException
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: " + username);
    }
}
