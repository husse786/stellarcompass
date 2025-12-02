package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.model.UserRole;
import ch.zhaw.stellarcompass.repository.UserRepository;
import ch.zhaw.stellarcompass.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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

        // Act & Assert
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("api@test.com"))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    void testGetAllUsersEndpoint() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testCreateUser_DuplicateEmail_ShouldReturnBadRequest() throws Exception {
        String duplicateUserJson = "{"
                + "\"email\": \"duplicate@test.com\","
                + "\"name\": \"Duplicate User\","
                + "\"role\": \"STUDENT\""
                + "}";

        when(userService.createUser(any(UserCreateDTO.class))).thenThrow(new RuntimeException("Duplicate user"));

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateUserJson))
                .andExpect(status().isBadRequest());
    }
}