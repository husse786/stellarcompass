package ch.zhaw.stellarcompass.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.model.UserRole;
import ch.zhaw.stellarcompass.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @SuppressWarnings("removal")
    @MockBean
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        // 1. Arrange: Testdata preparation
        // Wir simulieren ein DTO, wie es vom Controller käme (da wir keinen Setter haben, nutzen wir Mockito oder Reflection,
        // But since DTO often are just data containers, it's cleaner if the DTO had an AllArgs constructor or setters.
        // Since in last step we only have getter/NoArgs, we simulate the data flow here or briefly extend the DTO with setters/AllArgs for tests.
        UserCreateDTO userDTO = mock(UserCreateDTO.class);
        when(userDTO.getEmail()).thenReturn("test@service.com");
        when(userDTO.getName()).thenReturn("Service Tester");
        when(userDTO.getRole()).thenReturn(UserRole.STUDENT);
        when(userDTO.getAuth0Id()).thenReturn("auth0|service123");

        // We tell the mock repository: "When save() is called, return a User"
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("generated-db-id"); // Simulierte DB-ID
            return u;
        });

        // 2. Act: test the method
        User createdUser = userService.createUser(userDTO);

        // 3. Assert: check results
        assertNotNull(createdUser.getId());
        assertEquals("test@service.com", createdUser.getEmail());
        assertEquals(UserRole.STUDENT, createdUser.getRole());
        assertEquals("auth0|service123", createdUser.getAuth0Id());
        
        // Check if the repository was really called
        verify(userRepository, times(1)).save(any(User.class));
    }
}