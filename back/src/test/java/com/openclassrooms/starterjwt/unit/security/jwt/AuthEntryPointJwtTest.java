package com.openclassrooms.starterjwt.unit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    void commence_shouldReturnUnauthorizedResponseWithErrorDetails() throws Exception {
        //GIVEN : on prépare les mocks nécessaires pour la requête, la réponse et l’exception

        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Invalid token");

        // On capture la sortie JSON écrite dans le stream de la réponse
        ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                responseOutputStream.write(b);
            }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener listener) {}
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        //GIVEN pour vérifier setters sur le response
        doNothing().when(response).setContentType(anyString());
        doNothing().when(response).setStatus(anyInt());

        //WHEN : on appelle la méthode commence qui doit écrire la réponse
        authEntryPointJwt.commence(request, response, authException);

        //THEN : on vérifie que le status et le content type sont bien définis
        //que les methodes soient bien applée avec les bons argument
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // On vérifie que la sortie JSON correspond aux valeurs attendues
        String jsonResponse = responseOutputStream.toString("UTF-8");

        // Désérialisation du JSON pour vérifications précises
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        var body = mapper.readValue(jsonResponse, java.util.Map.class);

        assertThat(body).containsEntry("status", HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(body).containsEntry("error", "Unauthorized");
        assertThat(body).containsEntry("message", "Invalid token");
        assertThat(body).containsEntry("path", "/api/test");
    }
}

