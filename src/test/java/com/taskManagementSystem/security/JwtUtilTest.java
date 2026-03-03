package com.taskManagementSystem.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @AfterEach
    void tearDown() {
        // clear any static state if needed
    }

    @Test
    void generateAndValidateToken_extractEmail() {
        JwtUtil jwtUtil = new JwtUtil();

        // set secret and expiration using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "secret", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60_000L);

        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    @Test
    void validateToken_invalidToken() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "01234567890123456789012345678901");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60_000L);

        String badToken = "this.is.not.a.valid.token";
        assertFalse(jwtUtil.validateToken(badToken));
    }
}
