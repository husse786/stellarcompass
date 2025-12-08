package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.dao.DuplicateKeyException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.dto.UserUpdateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.model.UserRole;
import ch.zhaw.stellarcompass.repository.UserRepository;
import ch.zhaw.stellarcompass.security.TestSecurityConfig;
import ch.zhaw.stellarcompass.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User studentUser;
    private User mentorUser;

    @BeforeEach
    void setUp() {
        // Setup test users
        adminUser = new User();
        adminUser.setId("admin-id");
        adminUser.setEmail("admin@stellar.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setAuth0Id("auth0|admin");

        studentUser = new User();
        studentUser.setId("student-id");
        studentUser.setEmail("student@stellar.com");
        studentUser.setName("Student User");
        studentUser.setRole(UserRole.STUDENT);
        studentUser.setAuth0Id("auth0|student");

        mentorUser = new User();
        mentorUser.setId("mentor-id");
        mentorUser.setEmail("mentor@stellar.com");
        mentorUser.setName("Mentor User");
        mentorUser.setRole(UserRole.MENTOR);
        mentorUser.setAuth0Id("auth0|mentor");
    }

    @Test
    void testCreateUserEndpoint() throws Exception {
        // Arrange: Create a mock user that the service will return
        User mockUser = new User();
        mockUser.setId("test-id-123");
        mockUser.setEmail("api@test.com");
        mockUser.setName("API Tester");
        mockUser.setRole(UserRole.STUDENT);
        mockUser.setAuth0Id("auth0|api123");
        
        // Configure the mock to return this user when createUser is called
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(mockUser);

        String userJson = "{"
                + "\"email\": \"api@test.com\","
                + "\"name\": \"API Tester\","
                + "\"role\": \"STUDENT\","
                + "\"auth0Id\": \"auth0|api123\""
                + "}";

        // Act & Assert - ADDED Authorization header
        mockMvc.perform(post("/api/user")
                .header("Authorization", TestSecurityConfig.ADMIN) // Admin token added
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("api@test.com"))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    void testGetAllUsersEndpoint() throws Exception {
        // Mock the service to return a list of users
        when(userService.getAllUsers()).thenReturn(Arrays.asList(adminUser, studentUser, mentorUser));

        // ADDED Authorization header
        mockMvc.perform(get("/api/user")
                .header("Authorization", TestSecurityConfig.STUDENT)) // Any authenticated user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }
    
    @Test
    void testCreateUser_DuplicateEmail_ShouldReturnConflict() throws Exception {
        String duplicateUserJson = "{"
                + "\"email\": \"duplicate@test.com\","
                + "\"name\": \"Duplicate User\","
                + "\"role\": \"STUDENT\""
                + "}";

        when(userService.createUser(any(UserCreateDTO.class)))
                .thenThrow(new DuplicateKeyException("Duplicate user"));

        // ADDED Authorization header
        mockMvc.perform(post("/api/user")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateUserJson))
                .andExpect(status().isConflict()); // Expecting 409 Conflict due to duplicate
    }

    @Test
    void testGetMyProfile_Success() throws Exception {
        // Arrange - The JWT token contains "student@stellar.com" email
        String email = "student@stellar.com";
        
        // Mock repository to return the student user
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(studentUser));

        // Act & Assert
        mockMvc.perform(get("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Student User"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void testGetMyProfile_NotFound() throws Exception {
        // Arrange - User not in database
        String email = "student@stellar.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById("student-id")).thenReturn(Optional.of(studentUser));

        // Act & Assert - ADDED Authorization header
        mockMvc.perform(get("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("student-id"))
                .andExpect(jsonPath("$.email").value("student@stellar.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById("nonexistent-id")).thenReturn(Optional.empty());

        // Act & Assert - ADDED Authorization header
        mockMvc.perform(get("/api/user/nonexistent-id")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false); // Explicit false for clarity
        
        User updatedUser = new User();
        updatedUser.setId("student-id");
        updatedUser.setEmail("updated@stellar.com");
        updatedUser.setName("Updated Name");
        updatedUser.setRole(UserRole.STUDENT);
        
        when(userService.updateUser(eq("student-id"), any(UserCreateDTO.class))).thenReturn(updatedUser);

        String updateJson = "{"
                + "\"email\": \"updated@stellar.com\","
                + "\"name\": \"Updated Name\","
                + "\"role\": \"STUDENT\""
                + "}";

        // Act & Assert
        mockMvc.perform(put("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@stellar.com"))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testUpdateUser_AsMentor_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(true);
        
        User updatedUser = new User();
        updatedUser.setId("student-id");
        updatedUser.setEmail("updated@stellar.com");
        updatedUser.setName("Updated Name");
        updatedUser.setRole(UserRole.STUDENT);
        
        when(userService.updateUser(eq("student-id"), any(UserCreateDTO.class))).thenReturn(updatedUser);

        String updateJson = "{"
                + "\"email\": \"updated@stellar.com\","
                + "\"name\": \"Updated Name\","
                + "\"role\": \"STUDENT\""
                + "}";

        // Act & Assert
        mockMvc.perform(put("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUser_AsStudent_OwnProfile_Success() throws Exception {
        // Arrange - Student updates their own profile
        String studentId = "student-id";
        String studentEmail = "student@stellar.com";
        
        // Mock: Student is not Admin or Mentor
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        
        // Mock: Get email from token
        when(userService.getEmail()).thenReturn(studentEmail);
        
        // Mock: Find user by ID - email matches
        when(userService.getUserById(studentId)).thenReturn(Optional.of(studentUser));
        
        // Mock: Update succeeds
        User updatedUser = new User();
        updatedUser.setId(studentId);
        updatedUser.setEmail(studentEmail);
        updatedUser.setName("Updated Student Name");
        updatedUser.setRole(UserRole.STUDENT);
        
        when(userService.updateUser(eq(studentId), any(UserCreateDTO.class))).thenReturn(updatedUser);

        String updateJson = "{"
                + "\"email\": \"student@stellar.com\","
                + "\"name\": \"Updated Student Name\","
                + "\"role\": \"STUDENT\""
                + "}";

        // Act & Assert
        mockMvc.perform(put("/api/user/" + studentId)
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Student Name"));
    }

    @Test
    void testUpdateUser_AsStudent_OtherProfile_Forbidden() throws Exception {
        // Arrange - Student tries to update another user's profile
        String otherUserId = "admin-id";
        String studentEmail = "student@stellar.com";
        
        // Mock: Student is not Admin or Mentor
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        
        // Mock: Get email from token
        when(userService.getEmail()).thenReturn(studentEmail);
        
        // Mock: Find user by ID - email does NOT match (admin's email)
        when(userService.getUserById(otherUserId)).thenReturn(Optional.of(adminUser));

        String updateJson = "{"
                + "\"email\": \"hacker@stellar.com\","
                + "\"name\": \"Hacker\","
                + "\"role\": \"ADMIN\""
                + "}";

        // Act & Assert
        mockMvc.perform(put("/api/user/" + otherUserId)
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        doNothing().when(userService).deleteUser("student-id");

        // Act & Assert
        mockMvc.perform(delete("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to delete a user
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_AsMentor_Forbidden() throws Exception {
        // Arrange - Mentor tries to delete a user (only Admins can delete)
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/user/student-id")
                .header("Authorization", TestSecurityConfig.MENTOR))
                .andExpect(status().isForbidden());
    }

    // Test partial update - only bio
    @Test
    void testUpdateMyProfile_PartialUpdate_OnlyBio() throws Exception {
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO(null, "New Bio Text", null);
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Old Name");
        updatedUser.setBio("New Bio Text");
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("New Bio Text"));
    }

    // Test partial update - only avatarUrl
    @Test
    void testUpdateMyProfile_PartialUpdate_OnlyAvatar() throws Exception {
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO(null, null, "http://newavatar.jpg");
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Old Name");
        updatedUser.setAvatarUrl("http://newavatar.jpg");
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("http://newavatar.jpg"));
    }

    // Test update with empty/blank name - should be ignored
    @Test
    void testUpdateMyProfile_BlankName_ShouldBeIgnored() throws Exception {
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO("   ", "New Bio", null);
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Old Name"); // Name should NOT change
        updatedUser.setBio("New Bio");
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Old Name"));
    }

    // Test that admin can also update their own profile
    @Test
    void testUpdateMyProfile_AsAdmin_Success() throws Exception {
        String email = "admin@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO("Admin Updated", "Admin bio", null);
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Admin Updated");
        updatedUser.setBio("Admin bio");
        updatedUser.setRole(UserRole.ADMIN);
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Updated"))
                .andExpect(jsonPath("$.role").value("ADMIN")); // Role should NOT change
    }

    // Test that mentor can update their own profile
    @Test
    void testUpdateMyProfile_AsMentor_Success() throws Exception {
        String email = "mentor@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO("Mentor Name", "I mentor students", "http://mentor.jpg");
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Mentor Name");
        updatedUser.setBio("I mentor students");
        updatedUser.setAvatarUrl("http://mentor.jpg");
        updatedUser.setRole(UserRole.MENTOR);
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mentor Name"))
                .andExpect(jsonPath("$.bio").value("I mentor students"));
    }

    // Test user not found in database (edge case)
    @Test
    void testUpdateMyProfile_UserNotFound() throws Exception {
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", null, null);
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class)))
                .thenThrow(new RuntimeException("User not found with email: " + email));
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError());
    }

    // Test empty body (all fields null)
    @Test
    void testUpdateMyProfile_EmptyBody() throws Exception {
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO(null, null, null);
        
        User unchangedUser = new User();
        unchangedUser.setEmail(email);
        unchangedUser.setName("Old Name");
        unchangedUser.setBio("Old Bio");
        
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(unchangedUser);
        
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Old Name")); // Nothing changed
    }

    @Test
    void testUpdateMyProfile_Success() throws Exception {
        // Arrange
        String email = "student@stellar.com";
        UserUpdateDTO updateDTO = new UserUpdateDTO("Neuer Name", "Meine Bio", "http://avatar.jpg");
        
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setName("Neuer Name");
        updatedUser.setBio("Meine Bio");

        // Mock Service - FIXED: updateUserProfile instead of updateUser
        when(userService.updateUserProfile(eq(email), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/user/me")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Neuer Name"))
                .andExpect(jsonPath("$.bio").value("Meine Bio"));
    }

    @Test
    void testUpdateMyProfile_Unauthorized() throws Exception {
        // Versuchen ohne Token zuzugreifen
        mockMvc.perform(put("/api/user/me")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }
}