package com.onkar.chc.security;

import com.onkar.chc.security.jwtaction.JWTStuff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public class JWTStuffTest {

    private JWTStuff jwtStuff;

    private static final String TEST_SECRET = "YourSuperSecretKeyThatIsAtLeast64BytesLongForHMACSHA512AlgorithmSecurityPurpose2026!";

    @BeforeEach
    public void setUp() {
        jwtStuff = new JWTStuff();
        ReflectionTestUtils.setField(jwtStuff, "secret", TEST_SECRET);
    }

    @Test
    public void testGenerateAndValidateToken() {
        UserDetails userDetails = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_Patient")));

        String token = jwtStuff.generateToken(userDetails);
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());

        String username = jwtStuff.getUsernameFromToken(token);
        Assertions.assertEquals("testuser", username);

        Boolean isValid = jwtStuff.validateToken(token, userDetails);
        Assertions.assertTrue(isValid);
    }

    @Test
    public void testValidateTokenWrongUser() {
        UserDetails userDetails1 = new User("user1", "password", List.of(new SimpleGrantedAuthority("ROLE_Patient")));
        UserDetails userDetails2 = new User("user2", "password", List.of(new SimpleGrantedAuthority("ROLE_Patient")));

        String token = jwtStuff.generateToken(userDetails1);

        Boolean isValid = jwtStuff.validateToken(token, userDetails2);
        Assertions.assertFalse(isValid);
    }

    @Test
    public void testExpirationDateFuture() {
        UserDetails userDetails = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_Doctor")));

        String token = jwtStuff.generateToken(userDetails);
        java.util.Date expiration = jwtStuff.getExpirationDateFromToken(token);

        Assertions.assertNotNull(expiration);
        Assertions.assertTrue(expiration.after(new java.util.Date()));
    }
}
