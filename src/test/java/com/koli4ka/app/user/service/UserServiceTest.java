package com.koli4ka.app.user.service;

import com.koli4ka.app.exeption.EmailAlreadyExists;
import com.koli4ka.app.exeption.PhoneNumberAlreadyExists;
import com.koli4ka.app.exeption.UserNameAlreadyExists;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.web.dtos.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .phone("1234567890")
                .firstName("Test")
                .lastName("User")
                .imageUrl("http://example.com/image.jpg")
                .role(UserRole.USER)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void register_WhenUserDoesNotExist_ShouldSaveUser() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.getByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.getByPhone(registerRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        // Act
        userService.register(registerRequest);

        // Assert
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registerRequest.getPassword());
    }

    @Test
    void register_WhenUsernameExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UserNameAlreadyExists.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.getByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(EmailAlreadyExists.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenPhoneNumberExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.getByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.getByPhone(registerRequest.getPhoneNumber())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(PhoneNumberAlreadyExists.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getByUsername(testUser.getUsername());

        // Assert
        assertEquals(testUser, result);
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void getByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername(testUser.getUsername());

        // Assert
        assertTrue(result instanceof AuthenticationDetails);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getRole(), ((AuthenticationDetails) result).getRole());
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void getById_ShouldReturnUser() {
        // Arrange
        when(userRepository.getUserById(testUserId)).thenReturn(testUser);

        // Act
        User result = userService.getById(testUserId);

        // Assert
        assertEquals(testUser, result);
        verify(userRepository).getUserById(testUserId);
    }

    @Test
    void getChatInfo_ShouldReturnUsers() {
        // Arrange
        List<UUID> userIds = Arrays.asList(testUserId);
        when(userRepository.getUserById(testUserId)).thenReturn(testUser);

        // Act
        List<User> result = userService.getChatInfo(userIds);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).getUserById(testUserId);
    }

    @Test
    void getChatInfo_WhenUserNotFound_ShouldSkipUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        List<UUID> userIds = Arrays.asList(nonExistentId);
        when(userRepository.getUserById(nonExistentId)).thenReturn(null);

        // Act
        List<User> result = userService.getChatInfo(userIds);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).getUserById(nonExistentId);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }

    @Test
    void changeRole_WhenUserIsUser_ShouldChangeToAdmin() {
        // Arrange
        testUser.setRole(UserRole.USER);
        when(userRepository.getUserById(testUserId)).thenReturn(testUser);

        // Act
        userService.changeRole(testUserId);

        // Assert
        assertEquals(UserRole.ADMIN, testUser.getRole());
        verify(userRepository).save(testUser);
    }

    @Test
    void changeRole_WhenUserIsAdmin_ShouldChangeToUser() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.getUserById(testUserId)).thenReturn(testUser);

        // Act
        userService.changeRole(testUserId);

        // Assert
        assertEquals(UserRole.USER, testUser.getRole());
        verify(userRepository).save(testUser);
    }
} 