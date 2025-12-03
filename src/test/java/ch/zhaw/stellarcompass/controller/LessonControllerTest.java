package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.service.LessonService;

@SpringBootTest
@AutoConfigureMockMvc
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonService lessonService;

    // --- CREATE TESTS ---
    @Test
    void testCreateLesson_Success() throws Exception {
        Lesson mockLesson = new Lesson("Intro", "Content", "sub-1");
        mockLesson.setId("lesson-1");
        
        when(lessonService.createLesson(any(LessonCreateDTO.class))).thenReturn(mockLesson);

        // JSON body (without DTO setters we have to build it as a string)

       String json = "{\"title\": \"Intro\", \"content\": \"Content\", \"subjectId\": \"sub-1\", \"contentType\": \"TEXT\"}";

        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("lesson-1"))
                .andExpect(jsonPath("$.title").value("Intro"));
    }

    @Test
    void testCreateLesson_SubjectNotFound_Returns404() throws Exception {
        // Simulate service exception (caught by GlobalExceptionHandler)
        when(lessonService.createLesson(any(LessonCreateDTO.class)))
            .thenThrow(new NoSuchElementException("Subject not found"));

        String json = "{\"title\": \"Intro\", \"content\": \"Content\", \"subjectId\": \"invalid\"}";

        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound()); // Erwarte 404
    }

    @Test
    void testCreateLesson_Validation_Returns400() throws Exception {
        // Empty title should trigger @Valid (if dependency is present)
        String invalidJson = "{\"title\": \"\", \"content\": \"\", \"subjectId\": \"\"}";

        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest()); // Expected 400 Bad Request
    }

    // --- GET ALL TESTS ---
    @Test
    void testGetAllLessons_Success() throws Exception {
        when(lessonService.getAllLessons()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/lesson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- GET BY ID TESTS ---
    @Test
    void testGetLessonById_Success() throws Exception {
        Lesson l = new Lesson("Title", "Content", "sub-1");
        l.setId("lesson-1");
        // Note: Controller now calls getLessonById which returns Lesson directly (not Optional)
        
        when(lessonService.getLessonById("lesson-1")).thenReturn(l);

        mockMvc.perform(get("/api/lesson/lesson-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetLessonById_NotFound() throws Exception {
        when(lessonService.getLessonById("unknown"))
            .thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(get("/api/lesson/unknown"))
                .andExpect(status().isNotFound());
    }

    // --- GET BY SUBJECT TESTS ---
    @Test
    void testGetLessonsBySubject_Success() throws Exception {
        when(lessonService.getLessonsBySubject("sub-1")).thenReturn(Arrays.asList(new Lesson()));

        mockMvc.perform(get("/api/lesson/subject/sub-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- UPDATE TESTS ---
    @Test
    void testUpdateLesson_Success() throws Exception {
        Lesson updated = new Lesson("New Title", "Content", "sub-1");
        when(lessonService.updateLesson(eq("lesson-1"), any(LessonCreateDTO.class))).thenReturn(updated);

        String json = "{\"title\": \"New Title\", \"content\": \"C\", \"subjectId\": \"sub-1\"}";

        mockMvc.perform(put("/api/lesson/lesson-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void testUpdateLesson_NotFound() throws Exception {
        when(lessonService.updateLesson(eq("unknown"), any(LessonCreateDTO.class)))
            .thenThrow(new NoSuchElementException());

        String json = "{\"title\": \"New Title\", \"content\": \"C\", \"subjectId\": \"sub-1\"}";

        mockMvc.perform(put("/api/lesson/unknown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    // --- DELETE TESTS ---
    @Test
    void testDeleteLesson_Success() throws Exception {
        mockMvc.perform(delete("/api/lesson/lesson-1"))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    void testDeleteLesson_NotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(lessonService).deleteLesson("unknown");
        
        mockMvc.perform(delete("/api/lesson/unknown"))
                .andExpect(status().isNotFound());
    }
}