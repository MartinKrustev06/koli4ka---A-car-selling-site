package com.koli4ka.app.user;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationDetailsTest {

    @Test
    void testGetAuthorities() {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER);

        Collection<? extends GrantedAuthority> authorities = authDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    @Test
    void testGetUsername() {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER);

        assertEquals("testUser", authDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authDetails = new AuthenticationDetails(userId, "testUser", "password", UserRole.USER);

        assertEquals("password", authDetails.getPassword());
    }
}
