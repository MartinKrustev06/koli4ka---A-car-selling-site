package com.koli4ka.app.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldSaveUser_WhenUsernameIsAvailable() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setPhoneNumber("123456789");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setImageUrl("image.jpg");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        userService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        User existingUser = new User();
        existingUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(existingUser));

        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setPhoneNumber("123456789");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setImageUrl("image.jpg");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(request));

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void getByUsername_ShouldReturnUser_WhenUserExists() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@example.com");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        User result = userService.getByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("testUser"));

        assertEquals("Username not found", exception.getMessage());
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        UUID testUserId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setUsername("testUser");

        when(userRepository.getUserById(testUserId)).thenReturn(mockUser);

        User result = userService.getById(testUserId);

        assertNotNull(result);
        assertEquals(testUserId, result.getId());
    }

    @Test
    void loadUserByUsername_ShouldReturnAuthenticationDetails_WhenUserExists() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("hashedPassword");
        mockUser.setId(UUID.randomUUID());

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        AuthenticationDetails result = (AuthenticationDetails) userService.loadUserByUsername("testUser");

        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUser"));

        assertEquals("testUser", exception.getMessage());
    }



    @Test
    void getChatInfo_ShouldReturnListOfUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        User user1 = new User();
        user1.setId(userId1);
        user1.setUsername("User1");
        User user2 = new User();
        user2.setId(userId2);
        user2.setUsername("User2");

        when(userRepository.getUserById(userId1)).thenReturn(user1);
        when(userRepository.getUserById(userId2)).thenReturn(user2);

        List<User> result = userService.getChatInfo(Arrays.asList(userId1, userId2));

        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getUsername());
        assertEquals("User2", result.get(1).getUsername());
    }

    @Test
    void getChatInfo_ShouldReturnEmptyList_WhenUsersNotFound() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        when(userRepository.getUserById(userId1)).thenReturn(null);
        when(userRepository.getUserById(userId2)).thenReturn(null);

        List<User> result = userService.getChatInfo(Arrays.asList(userId1, userId2));

        assertTrue(result.isEmpty());
    }
}
