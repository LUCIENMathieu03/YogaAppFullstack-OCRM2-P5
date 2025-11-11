package com.openclassrooms.starterjwt.unit.security.jwt;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @Mock
    Authentication authentication;


    @InjectMocks
    JwtUtils jwtUtils;


    String jwtSecret = "fake.jwt.token";
    int jwtExpirationMs = 15000;

    @BeforeEach
    void setUp() throws Exception {
        // Injection manuelle des valeurs @Value (comme elles ne sont pas injectées automatiquement ici)
        Field jwtSecretField = JwtUtils.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(jwtUtils, jwtSecret);

        Field jwtExpirationField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.setInt(jwtUtils, jwtExpirationMs);
    }

    @Test
    void generateJwtToken_shouldGenerateAToken(){
        //GIVEN
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        //WHEN
        String token = jwtUtils.generateJwtToken(authentication);

        //THEN : le token ne doit pas être null et doit contenir le username encodé
        assertThat(token).isNotNull();

        String usernameFromToken = jwtUtils.getUserNameFromJwtToken(token);
        assertThat(usernameFromToken).isEqualTo("testUser");
    }


    @Test
    void getUserNameFromJwtToken_shouldReturnCorrectUsername() {
        // GIVEN : un token généré avec un username connu
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder().username("testuser").build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        String token = jwtUtils.generateJwtToken(authentication);

        // WHEN : récupération du username depuis le token
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // THEN : le username doit correspondre
        assertThat(username).isEqualTo("testuser");
    }


    @Test
    void validateJwtToken_shouldReturnTrueForValidToken() {
        // GIVEN : un token valide généré
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder().username("testuser").build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        String token = jwtUtils.generateJwtToken(authentication);

        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(valid).isTrue();
    }

    @Test //Fonctionne pour la SignatureException, le MalformedJwtException et le UnsupportedJwtException
    void validateJwtToken_shouldCatchException_andReturnFalse() {
        // GIVEN
        String badToken = "this.is.an.invalid.jwt";

        // WHEN
        boolean result = jwtUtils.validateJwtToken(badToken);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void validateJwtToken_shouldCatchExpiredJwtException_andReturnFalse() {
        // GIVEN : un token avec expiration passée
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))        // émision 10s dans le passé
                .setExpiration(new Date(System.currentTimeMillis() - 5000))      // expiration 5s dans le passé
                .signWith(SignatureAlgorithm.HS512, "fake.jwt.token")
                .compact();

        // WHEN
        boolean result = jwtUtils.validateJwtToken(expiredToken);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void validateJwtToken_shouldCatchIllegalArgumentException_andReturnFalse() {
        // GIVEN : un token vide ou null
        String emptyToken = "";

        // WHEN
        boolean result = jwtUtils.validateJwtToken(emptyToken);

        // THEN
        assertThat(result).isFalse();
    }


}
