package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.security.TestSecurityConfig;
import ch.zhaw.stellarcompass.service.LessonService;
import ch.zhaw.stellarcompass.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonService lessonService;

    @MockBean
    private UserService userService;

    private Lesson introLesson;
    private Lesson advancedLesson;

    @BeforeEach
    void setUp() {
        introLesson = new Lesson("Intro to Java", "Basic Java concepts", "sub-1");
        introLesson.setId("lesson-1");

        advancedLesson = new Lesson("Advanced Java", "Advanced topics", "sub-1");
        advancedLesson.setId("lesson-2");
    }

    // ==================== CREATE Tests ====================

    @Test
    void testCreateLesson_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        when(lessonService.createLesson(any(LessonCreateDTO.class))).thenReturn(introLesson);

        String json = "{\"title\": \"Intro to Java\", \"content\": \"Basic Java concepts\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("lesson-1"))
                .andExpect(jsonPath("$.title").value("Intro to Java"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void testCreateLesson_AsMentor_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(true);
        when(lessonService.createLesson(any(LessonCreateDTO.class))).thenReturn(advancedLesson);

        String json = "{\"title\": \"Advanced Java\", \"content\": \"Advanced topics\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("lesson-2"))
                .andExpect(jsonPath("$.title").value("Advanced Java"));
    }

    @Test
    void testCreateLesson_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to create a lesson
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        String json = "{\"title\": \"Hacking\", \"content\": \"Not allowed\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateLesson_Unauthenticated_Forbidden() throws Exception {
        // Arrange - No authorization header
        String json = "{\"title\": \"Test\", \"content\": \"Test\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateLesson_SubjectNotFound_Returns404() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);
        when(lessonService.createLesson(any(LessonCreateDTO.class)))
                .thenThrow(new NoSuchElementException("Subject not found"));

        String json = "{\"title\": \"Intro\", \"content\": \"Content\", \"subjectId\": \"invalid\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateLesson_Validation_Returns400() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        // Empty title should trigger @Valid
        String invalidJson = "{\"title\": \"\", \"content\": \"\", \"subjectId\": \"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/lesson")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== READ Tests ====================

    @Test
    void testGetAllLessons_AsStudent_Success() throws Exception {
        // Arrange
        when(lessonService.getAllLessons()).thenReturn(Arrays.asList(introLesson, advancedLesson));

        // Act & Assert
        mockMvc.perform(get("/api/lesson")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Intro to Java"))
                .andExpect(jsonPath("$[1].title").value("Advanced Java"));
    }

    @Test
    void testGetAllLessons_EmptyList() throws Exception {
        // Arrange
        when(lessonService.getAllLessons()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/lesson")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllLessons_Unauthenticated_Forbidden() throws Exception {
        // Act & Assert - No authorization header
        mockMvc.perform(get("/api/lesson"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetLessonById_Success() throws Exception {
        // Arrange
        when(lessonService.getLessonById("lesson-1")).thenReturn(introLesson);

        // Act & Assert
        mockMvc.perform(get("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lesson-1"))
                .andExpect(jsonPath("$.title").value("Intro to Java"));
    }

    @Test
    void testGetLessonById_NotFound() throws Exception {
        // Arrange
        when(lessonService.getLessonById("unknown"))
                .thenThrow(new NoSuchElementException("Not found"));

        // Act & Assert
        mockMvc.perform(get("/api/lesson/unknown")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetLessonsBySubject_Success() throws Exception {
        // Arrange
        when(lessonService.getLessonsBySubject("sub-1"))
                .thenReturn(Arrays.asList(introLesson, advancedLesson));

        // Act & Assert
        mockMvc.perform(get("/api/lesson/subject/sub-1")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetLessonsBySubject_EmptyList() throws Exception {
        // Arrange
        when(lessonService.getLessonsBySubject("sub-999"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/lesson/subject/sub-999")
                .header("Authorization", TestSecurityConfig.MENTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE Tests ====================

    @Test
    void testUpdateLesson_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        Lesson updatedLesson = new Lesson("Updated Title", "Updated content", "sub-1");
        updatedLesson.setId("lesson-1");

        when(lessonService.updateLesson(eq("lesson-1"), any(LessonCreateDTO.class)))
                .thenReturn(updatedLesson);

        String json = "{\"title\": \"Updated Title\", \"content\": \"Updated content\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(put("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void testUpdateLesson_AsMentor_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(true);

        Lesson updatedLesson = new Lesson("Mentor Update", "New content", "sub-1");
        updatedLesson.setId("lesson-2");

        when(lessonService.updateLesson(eq("lesson-2"), any(LessonCreateDTO.class)))
                .thenReturn(updatedLesson);

        String json = "{\"title\": \"Mentor Update\", \"content\": \"New content\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(put("/api/lesson/lesson-2")
                .header("Authorization", TestSecurityConfig.MENTOR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mentor Update"));
    }

    @Test
    void testUpdateLesson_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to update a lesson
        when(userService.userHasRole("ADMIN")).thenReturn(false);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        String json = "{\"title\": \"Hacked\", \"content\": \"Not allowed\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(put("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateLesson_NotFound() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        when(lessonService.updateLesson(eq("unknown"), any(LessonCreateDTO.class)))
                .thenThrow(new NoSuchElementException("Lesson not found"));

        String json = "{\"title\": \"New Title\", \"content\": \"C\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        // Act & Assert
        mockMvc.perform(put("/api/lesson/unknown")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateLesson_Validation_Returns400() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        when(userService.userHasRole("MENTOR")).thenReturn(false);

        String invalidJson = "{\"title\": \"\", \"content\": \"\", \"subjectId\": \"\"}";

        // Act & Assert
        mockMvc.perform(put("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE Tests ====================

    @Test
    void testDeleteLesson_AsAdmin_Success() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        doNothing().when(lessonService).deleteLesson("lesson-1");

        // Act & Assert
        mockMvc.perform(delete("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteLesson_AsMentor_Forbidden() throws Exception {
        // Arrange - Mentor tries to delete (only Admin can delete)
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.MENTOR))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteLesson_AsStudent_Forbidden() throws Exception {
        // Arrange - Student tries to delete
        when(userService.userHasRole("ADMIN")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/lesson/lesson-1")
                .header("Authorization", TestSecurityConfig.STUDENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteLesson_NotFound() throws Exception {
        // Arrange
        when(userService.userHasRole("ADMIN")).thenReturn(true);
        doThrow(new NoSuchElementException("Lesson not found"))
                .when(lessonService).deleteLesson("unknown");

        // Act & Assert
        mockMvc.perform(delete("/api/lesson/unknown")
                .header("Authorization", TestSecurityConfig.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteLesson_Unauthenticated_Forbidden() throws Exception {
        // Act & Assert - No authorization header
        mockMvc.perform(delete("/api/lesson/lesson-1"))
                .andExpect(status().isForbidden());
    }
}