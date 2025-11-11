package com.openclassrooms.starterjwt.unit.security.jwt;


import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestAuthTokenFilter extends AuthTokenFilter {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);
    }
}

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {
    @Mock
    HttpServletResponse response;

    @Mock
    HttpServletRequest request;

    @Mock
    FilterChain filterChain;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserDetails userDetails;

    @InjectMocks
    TestAuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void doFilterInternal_shouldContinueFilterChain_whenTokenIsNull() throws ServletException, IOException {
        //GIVEN : le header Authorization est absent (token null)
        when(request.getHeader("Authorization")).thenReturn(null);

        //WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        //THEN
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldContinueFilterChain_whenTokenIsInvalid() throws ServletException, IOException {
        //GIVEN : le header Authorization est absent (token null)
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        //WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        //THEN
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldLogExceptionAndContinueFilterChain() throws Exception {
        //GIVEN
        String token = "token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("Test Exception"));

        //WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        //THEN
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldAuthenticateUser_whenTokenIsValid() throws ServletException, IOException {
        //GIVEN : le token est valid
        String token = "valid.jwt.token";
        String ValidUserName = "user@example.com";
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("encodedPassword")
                .firstName("totoFirst")
                .lastName("totoLast")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(ValidUserName);

        when(userDetailsService.loadUserByUsername(ValidUserName)).thenReturn(userDetails);


        //WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
        verify(filterChain).doFilter(request, response);
    }





}
