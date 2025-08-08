package com.sl.shortlink.controller;

import com.sl.shortlink.model.security.AuthRequest;
import com.sl.shortlink.model.security.AuthResponse;
import com.sl.shortlink.security.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_shouldReturnAuthResponse() {
        AuthRequest request = new AuthRequest("user", "pass");
        AuthResponse response = new AuthResponse("token");

        when(authService.login(request)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("token", result.getBody().token());
    }

    @Test
    void register_shouldReturnSuccessMessage() {
        AuthRequest request = new AuthRequest("user", "pass");
        String message = "User registered";

        when(authService.registry(request)).thenReturn(message);

        ResponseEntity<String> result = authController.register(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(message, result.getBody());
    }

}