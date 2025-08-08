package com.sl.shortlink.security;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSecret("MySuperSecretKeyForJwtTokenMySuperSecretKey");
    }

    @Test
    void generateTokenAndExtractUsername() {
        String username = "testuser";

        String token = jwtUtil.generateToken(username);
        assertNotNull(token);

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String username = "validUser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.string";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

}