package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void findAll_shouldReturnAllSessions() {
        //GIVEN
        Session s1 = new Session();
        Session s2 = new Session();
        List<Session> sessions = Arrays.asList(s1, s2);

        when(sessionRepository.findAll()).thenReturn(sessions);
        //WHEN
        List<Session> result = sessionService.findAll();
        //THEN
        assertThat(result).isEqualTo(sessions);
        verify(sessionRepository).findAll();
    }

    @Test
    void findById_shouldReturnSession_whenExists() {
        //GIVEN
        Long id = 1234L;
        Session session = new Session();
        when(sessionRepository.findById(id)).thenReturn(Optional.of(session));
        //WHEN
        Session result = sessionService.getById(id);
        //THEN
        assertThat(result).isEqualTo(session);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        //GIVEN
        Long id = 1234L;
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());
        //WHEN
        Session result = sessionService.getById(id);
        //THEN
        assertThat(result).isNull();
    }

    @Test
    void create_shouldSaveSession() {
        //GIVEN
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);
        //GIVEN
        Session result = sessionService.create(session);
        //THEN
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        //GIVEN
        Long id = 1234L;
        //WHEN
        sessionService.delete(id);
        //THEN
        verify(sessionRepository).deleteById(id);
    }

    @Test
    void update_shouldSetIdAndSave() {
        //GIVEN
        Long id = 1234L;
        Session session = new Session();

        when(sessionRepository.save(session)).thenReturn(session);
        //WHEN
        Session result = sessionService.update(id, session);
        //THEN
        assertThat(session.getId()).isEqualTo(id);
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldAddUser_WhenNotAlreadyParticipate() {
        //GIVEN
        Long sessionId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);
        //WHEN
        sessionService.participate(sessionId, userId);
        //THEN
        assertThat(session.getUsers().toArray()).contains(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFoundException_whenSessionOrUserNotFound() {
        //GIVEN
        Long sessionId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>());

        //CAS 1 : Session inexistante, utilisateur inexistant
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //WHEN - THEN
        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));

        //CAS 2 : Session existe, utilisateur inexistant
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //WHEN - THEN
        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    void participate_shouldThrowBadRequestException_whenUserAlreadyParticipates() {
        //GIVEN :
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>(List.of(user)));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //WHEN - THEN
        assertThrows(BadRequestException.class, () -> {
            sessionService.participate(1L, userId);
        });
    }

    @Test
    void noLongerParticipate_shouldRemoveUser_WhenParticipates() {
        //GIVEN
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(Arrays.asList(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);
        //WHEN
        sessionService.noLongerParticipate(1L, userId);
        //THEN
        assertThat(session.getUsers().toArray()).doesNotContain(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionNotFound() {
        //GIVEN
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        //WHEN + THEN
        assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestException_whenUserNotParticipating() {
        //GIVEN
        Long userId = 1L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        //WHEN + THEN
        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(1L, userId);
        });
    }

}
