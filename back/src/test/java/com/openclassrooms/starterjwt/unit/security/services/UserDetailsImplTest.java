package com.openclassrooms.starterjwt.unit.security.services;


import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserDetailsImplTest {

    @Test
    void getters_shouldReturnCorrectValues() {
        // GIVEN
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(123L)
                .username("user@example.com")
                .firstName("user")
                .lastName("exemple")
                .admin(true)
                .password("password")
                .build();

        // WHEN - THEN
        assertThat(user.getId()).isEqualTo(123L);
        assertThat(user.getUsername()).isEqualTo("user@example.com");
        assertThat(user.getFirstName()).isEqualTo("user");
        assertThat(user.getLastName()).isEqualTo("exemple");
        assertThat(user.getAdmin()).isEqualTo(true);
        assertThat(user.getPassword()).isEqualTo("password");
    }

    @Test
    void userDetailsInterfaceMethods_shouldReturnTrue() {
        // GIVEN : un userDetails
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("u")
                .password("p")
                .build();

        // WHEN / THEN : méthodes de UserDetails retournent true
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void getAuthorities_shouldReturnEmptySet() {
        // GIVEN :
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("u")
                .password("p")
                .build();

        // WHEN
        Collection<?> authorities = user.getAuthorities();

        // THEN : donne une collection vide (set)
        assertThat(authorities).isEmpty();
    }

    @Test
    void equals_shouldReturnTrue_forSameId() {
        // GIVEN : deux instances avec le même id
        Long id = 1L;
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(id).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(id).build();

        // WHEN / THEN : les deux objets doivent être égaux
        assertThat(user1).isEqualTo(user2);  // appelle user1.equals(user2)
    }

    @Test
    void equals_shouldReturnFalse_forDifferentId() {
        // GIVEN : deux instances avec des id différents
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();

        // WHEN / THEN : les deux objets ne doivent pas être égaux
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equals_shouldReturnFalse_forNullAndDifferentType() {
        // GIVEN : une instance UserDetailsImpl
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        // WHEN / THEN : comparaison avec null et un autre type doit renvoyer false
        assertThat(user).isNotEqualTo(null);
        assertThat(user).isNotEqualTo("une chaîne");
    }

    @Test
    void equals_shouldReturnTrue_forSameReference() {
        // GIVEN : une seule instance
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        // WHEN / THEN : un objet est égal à lui-même
        assertThat(user).isEqualTo(user);
    }


}
