package ch.zhaw.stellarcompass.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.model.UserRole;
import ch.zhaw.stellarcompass.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User testStudent;
    private User testMentor;
    private User testAdmin;
    private UserCreateDTO validDTO;

    @BeforeEach
    void setUp() {
        // Setup test users
        testStudent = new User("student@service.com", "Test Student", UserRole.STUDENT);
        testStudent.setId("student-id");
        testStudent.setAuth0Id("auth0|student123");

        testMentor = new User("mentor@service.com", "Test Mentor", UserRole.MENTOR);
        testMentor.setId("mentor-id");
        testMentor.setAuth0Id("auth0|mentor456");

        testAdmin = new User("admin@service.com", "Test Admin", UserRole.ADMIN);
        testAdmin.setId("admin-id");
        testAdmin.setAuth0Id("auth0|admin789");

        // Setup valid DTO
        validDTO = mock(UserCreateDTO.class);
        when(validDTO.getEmail()).thenReturn("test@service.com");
        when(validDTO.getName()).thenReturn("Service Tester");
        when(validDTO.getRole()).thenReturn(UserRole.STUDENT);
        when(validDTO.getAuth0Id()).thenReturn("auth0|service123");
    }

    // ==================== CREATE Tests ====================

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("generated-db-id");
            return u;
        });

        // Act
        User createdUser = userService.createUser(validDTO);

        // Assert
        assertNotNull(createdUser.getId());
        assertEquals("test@service.com", createdUser.getEmail());
        assertEquals("Service Tester", createdUser.getName());
        assertEquals(UserRole.STUDENT, createdUser.getRole());
        assertEquals("auth0|service123", createdUser.getAuth0Id());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_WithoutAuth0Id() {
        // Arrange
        when(validDTO.getAuth0Id()).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User createdUser = userService.createUser(validDTO);

        // Assert
        assertNull(createdUser.getAuth0Id(), "Auth0Id should be null if not provided");
        assertEquals("test@service.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_DifferentRoles() {
        // Test with ADMIN role
        when(validDTO.getRole()).thenReturn(UserRole.ADMIN);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User adminUser = userService.createUser(validDTO);
        assertEquals(UserRole.ADMIN, adminUser.getRole());

        // Test with MENTOR role
        when(validDTO.getRole()).thenReturn(UserRole.MENTOR);
        User mentorUser = userService.createUser(validDTO);
        assertEquals(UserRole.MENTOR, mentorUser.getRole());
    }

    // ==================== READ Tests ====================

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testStudent, testMentor, testAdmin));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertEquals(3, users.size());
        assertEquals("student@service.com", users.get(0).getEmail());
        assertEquals("mentor@service.com", users.get(1).getEmail());
        assertEquals("admin@service.com", users.get(2).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById("student-id")).thenReturn(Optional.of(testStudent));

        // Act
        Optional<User> found = userService.getUserById("student-id");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("student-id", found.get().getId());
        assertEquals("student@service.com", found.get().getEmail());
        verify(userRepository, times(1)).findById("student-id");
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act
        Optional<User> found = userService.getUserById("unknown");

        // Assert
        assertFalse(found.isPresent());
        verify(userRepository, times(1)).findById("unknown");
    }

    @Test
    void testGetUserById_NullId() {
        // Arrange
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<User> found = userService.getUserById(null);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testGetUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("mentor@service.com")).thenReturn(Optional.of(testMentor));

        // Act
        Optional<User> found = userService.getUserByEmail("mentor@service.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("mentor@service.com", found.get().getEmail());
        assertEquals(UserRole.MENTOR, found.get().getRole());
        verify(userRepository, times(1)).findByEmail("mentor@service.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@service.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> found = userService.getUserByEmail("nonexistent@service.com");

        // Assert
        assertFalse(found.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@service.com");
    }

    @Test
    void testGetUserByEmail_NullEmail() {
        // Arrange
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act
        Optional<User> found = userService.getUserByEmail(null);

        // Assert
        assertFalse(found.isPresent());
    }

    // ==================== UPDATE Tests ====================

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UserCreateDTO updateDTO = mock(UserCreateDTO.class);
        when(updateDTO.getEmail()).thenReturn("updated@service.com");
        when(updateDTO.getName()).thenReturn("Updated Name");
        when(updateDTO.getRole()).thenReturn(UserRole.MENTOR);

        when(userRepository.findById("student-id")).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User updated = userService.updateUser("student-id", updateDTO);

        // Assert
        assertEquals("updated@service.com", updated.getEmail());
        assertEquals("Updated Name", updated.getName());
        assertEquals(UserRole.MENTOR, updated.getRole());
        verify(userRepository, times(1)).findById("student-id");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_KeepsAuth0Id() {
        // Arrange
        UserCreateDTO updateDTO = mock(UserCreateDTO.class);
        when(updateDTO.getEmail()).thenReturn("updated@service.com");
        when(updateDTO.getName()).thenReturn("Updated Name");
        when(updateDTO.getRole()).thenReturn(UserRole.STUDENT);

        when(userRepository.findById("student-id")).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User updated = userService.updateUser("student-id", updateDTO);

        // Assert - Auth0Id should remain unchanged
        assertEquals("auth0|student123", updated.getAuth0Id(), "Auth0Id should not be modified during update");
    }

    @Test
    void testUpdateUser_NotFound_ThrowsException() {
        // Arrange
        UserCreateDTO updateDTO = mock(UserCreateDTO.class);
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> userService.updateUser("unknown", updateDTO)
        );

        assertTrue(exception.getMessage().contains("User mit ID unknown nicht gefunden"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_AllFields() {
        // Arrange - Update all possible fields
        UserCreateDTO updateDTO = mock(UserCreateDTO.class);
        when(updateDTO.getEmail()).thenReturn("new@email.com");
        when(updateDTO.getName()).thenReturn("Completely New Name");
        when(updateDTO.getRole()).thenReturn(UserRole.ADMIN);

        when(userRepository.findById("mentor-id")).thenReturn(Optional.of(testMentor));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User updated = userService.updateUser("mentor-id", updateDTO);

        // Assert
        assertEquals("new@email.com", updated.getEmail());
        assertEquals("Completely New Name", updated.getName());
        assertEquals(UserRole.ADMIN, updated.getRole());
    }

    // ==================== DELETE Tests ====================

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.existsById("admin-id")).thenReturn(true);
        doNothing().when(userRepository).deleteById("admin-id");

        // Act
        assertDoesNotThrow(() -> userService.deleteUser("admin-id"));

        // Assert
        verify(userRepository, times(1)).existsById("admin-id");
        verify(userRepository, times(1)).deleteById("admin-id");
    }

    @Test
    void testDeleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById("unknown")).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> userService.deleteUser("unknown")
        );

        assertTrue(exception.getMessage().contains("User mit ID unknown nicht gefunden"));
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void testDeleteUser_NullId_ThrowsException() {
        // Arrange
        when(userRepository.existsById(null)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(null));
        verify(userRepository, never()).deleteById(anyString());
    }

    // ==================== Security Context Tests ====================

    @Test
    void testUserHasRole_WithValidRole() {
        // Arrange - Mock JWT with roles
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("user_roles")).thenReturn(Arrays.asList("ADMIN", "MENTOR"));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean hasAdmin = userService.userHasRole("ADMIN");
        boolean hasMentor = userService.userHasRole("MENTOR");
        boolean hasStudent = userService.userHasRole("STUDENT");

        // Assert
        assertTrue(hasAdmin, "User should have ADMIN role");
        assertTrue(hasMentor, "User should have MENTOR role");
        assertFalse(hasStudent, "User should not have STUDENT role");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUserHasRole_WithNoRoles() {
        // Arrange - JWT with no roles
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("user_roles")).thenReturn(Collections.emptyList());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean hasAdmin = userService.userHasRole("ADMIN");

        // Assert
        assertFalse(hasAdmin, "User should not have any roles");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUserHasRole_WithNullRoles() {
        // Arrange - JWT with null roles
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("user_roles")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean hasAdmin = userService.userHasRole("ADMIN");

        // Assert
        assertFalse(hasAdmin, "User should not have roles when claim is null");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUserHasRole_NotAuthenticated() {
        // Arrange - No JWT (not authenticated)
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymousUser"); // String instead of JWT

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean hasAdmin = userService.userHasRole("ADMIN");

        // Assert
        assertFalse(hasAdmin, "Unauthenticated user should not have any roles");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetEmail_Success() {
        // Arrange - Mock JWT with email
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("email")).thenReturn("user@test.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String email = userService.getEmail();

        // Assert
        assertEquals("user@test.com", email);

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetEmail_NotAuthenticated() {
        // Arrange - No JWT
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String email = userService.getEmail();

        // Assert
        assertNull(email, "Email should be null when not authenticated");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetEmail_NullEmail() {
        // Arrange - JWT without email claim
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("email")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String email = userService.getEmail();

        // Assert
        assertNull(email, "Email should be null when claim is not present");

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    // ==================== Integration Tests ====================

    @Test
    void testCreateAndRetrieve_Integration() {
        // Arrange
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId("new-user-id");
            return saved;
        });
        when(userRepository.findById("new-user-id")).thenReturn(Optional.of(testStudent));

        // Act
        User created = userService.createUser(validDTO);
        Optional<User> retrieved = userService.getUserById("new-user-id");

        // Assert
        assertNotNull(created);
        assertTrue(retrieved.isPresent());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById("new-user-id");
    }

    @Test
    void testUpdateAndRetrieve_Integration() {
        // Arrange
        UserCreateDTO updateDTO = mock(UserCreateDTO.class);
        when(updateDTO.getEmail()).thenReturn("updated@test.com");
        when(updateDTO.getName()).thenReturn("Updated Name");
        when(updateDTO.getRole()).thenReturn(UserRole.MENTOR);

        when(userRepository.findById("student-id")).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User updated = userService.updateUser("student-id", updateDTO);
        @SuppressWarnings("unused")
        Optional<User> retrieved = userService.getUserById("student-id");

        // Assert
        assertEquals("updated@test.com", updated.getEmail());
        verify(userRepository, times(2)).findById("student-id"); // Once for update, once for retrieve
    }
}