package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.security.TestSecurityConfig;
import ch.zhaw.stellarcompass.service.SubjectService;
import ch.zhaw.stellarcompass.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectService subjectService;

    @MockBean
    private UserService userService;

    private Subject mathSubject;
    private Subject csSubject;

    @BeforeEach
    void setUp() {
        mathSubject = new Subject("Mathematik", "Grundlagen der Mathematik");
        mathSubject.setId("math-123");

        csSubject = new Subject("Informatik", "Programmieren mit Java");
        csSubject.setId("cs-456");
    }

    // ==================== CREATE Tests ====================

    @Test
    void testCreateSubject_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        when(subjectService.createSubject(any(SubjectCreateDTO.class))).thenReturn(csSubject);

        String jsonBody = "{\"title\": \"Informatik\", \"description\": \"Programmieren mit Java\"}";

        // Act & Assert
        mockMvc.perform(post("/api/subject")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cs-456"))
                .andExpect(jsonPath("$.title").value("Informatik"))
                .andExpect(jsonPath("$.description").value("Programmieren mit Java"));
    }

    @Test
    void testCreateSubject_AsMentor_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(true);
        when(subjectService.createSubject(any(SubjectCreateDTO.class))).thenReturn(mathSubject);

        String jsonBody = "{\"title\": \"Mathematik\", \"description\": \"Grundlagen der Mathematik\"}";

        // Act & Assert
        mockMvc.perform(post("/api/subject")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("math-123"))
                .andExpect(jsonPath("$.title").value("Mathematik"));
    }

    @Test
    void testCreateSubject_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to create a subject
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        String jsonBody = "{\"title\": \"Hacking\", \"description\": \"Not allowed\"}";

        // Act & Assert
        mockMvc.perform(post("/api/subject")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateSubject_Unauthenticated_Unauthorized() throws Exception {
        // Arrange - No authorization header
        String jsonBody = "{\"title\": \"Test\", \"description\": \"Test\"}";

        // Act & Assert
        mockMvc.perform(post("/api/subject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isForbidden());
    }

    // ==================== READ Tests ====================

    @Test
    void testGetAllSubjects_AsStudent_Success() throws Exception {
        // Arrange
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(mathSubject, csSubject));

        // Act & Assert
        mockMvc.perform(get("/api/subject")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Mathematik"))
                .andExpect(jsonPath("$[1].title").value("Informatik"));
    }

    @Test
    void testGetAllSubjects_AsAdmin_Success() throws Exception {
        // Arrange
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(mathSubject));

        // Act & Assert
        mockMvc.perform(get("/api/subject")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllSubjects_EmptyList() throws Exception {
        // Arrange
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/subject")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllSubjects_Unauthenticated_Unauthorized() throws Exception {
        // Act & Assert - No authorization header
        mockMvc.perform(get("/api/subject"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== UPDATE Tests ====================

    @Test
    void testUpdateSubject_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        Subject updatedSubject = new Subject("Informatik Advanced", "Fortgeschrittene Programmierung");
        updatedSubject.setId("cs-456");
        
        when(subjectService.updateSubject(eq("cs-456"), any(SubjectCreateDTO.class)))
                .thenReturn(updatedSubject);

        String jsonBody = "{\"title\": \"Informatik Advanced\", \"description\": \"Fortgeschrittene Programmierung\"}";

        // Act & Assert
        mockMvc.perform(put("/api/subject/cs-456")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Informatik Advanced"))
                .andExpect(jsonPath("$.description").value("Fortgeschrittene Programmierung"));
    }

    @Test
    void testUpdateSubject_AsMentor_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(true);

        Subject updatedSubject = new Subject("Mathematik II", "Analysis");
        updatedSubject.setId("math-123");
        
        when(subjectService.updateSubject(eq("math-123"), any(SubjectCreateDTO.class)))
                .thenReturn(updatedSubject);

        String jsonBody = "{\"title\": \"Mathematik II\", \"description\": \"Analysis\"}";

        // Act & Assert
        mockMvc.perform(put("/api/subject/math-123")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mathematik II"));
    }

    @Test
    void testUpdateSubject_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to update a subject
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        String jsonBody = "{\"title\": \"Hacked\", \"description\": \"Not allowed\"}";

        // Act & Assert
        mockMvc.perform(put("/api/subject/cs-456")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateSubject_NotFound() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        
        when(subjectService.updateSubject(eq("nonexistent-id"), any(SubjectCreateDTO.class)))
                .thenThrow(new RuntimeException("Subject not found"));

        String jsonBody = "{\"title\": \"Test\", \"description\": \"Test\"}";

        // Act & Assert
        mockMvc.perform(put("/api/subject/nonexistent-id")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE Tests ====================

    @Test
    void testDeleteSubject_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        doNothing().when(subjectService).deleteSubject("cs-456");

        // Act & Assert
        mockMvc.perform(delete("/api/subject/cs-456")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSubject_AsMentor_Forbidden() throws Exception {
        // Arrange - Mentor tries to delete (only Admin can delete)
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/subject/cs-456")
                .header("Authorization", TestSecurityConfig.MENTOR))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteSubject_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to delete
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/subject/cs-456")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteSubject_Unauthenticated_Unauthorized() throws Exception {
        // Act & Assert - No authorization header
        mockMvc.perform(delete("/api/subject/cs-456"))
                .andExpect(status().isForbidden());
    }
}