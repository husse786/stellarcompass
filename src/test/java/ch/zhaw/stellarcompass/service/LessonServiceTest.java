package ch.zhaw.stellarcompass.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.repository.LessonRepository;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

@SpringBootTest
class LessonServiceTest {

    @Autowired
    private LessonService lessonService;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private SubjectRepository subjectRepository;

    // --- CREATE TESTS ---

    @Test
    void testCreateLesson_HappyPath() {
        // Arrange
        LessonCreateDTO dto = mock(LessonCreateDTO.class);
        when(dto.getTitle()).thenReturn("Mathe 1");
        when(dto.getContent()).thenReturn("Inhalt");
        when(dto.getSubjectId()).thenReturn("sub-1");
        when(dto.getContentType()).thenReturn("TEXT");

        // We simulate: The subject exists
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        // We simulate: Saving returns the object
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson created = lessonService.createLesson(dto);

        // Assert
        assertEquals("Mathe 1", created.getTitle());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void testCreateLesson_InvalidSubject_ThrowsException() {
        // Arrange
        LessonCreateDTO dto = mock(LessonCreateDTO.class);
        when(dto.getSubjectId()).thenReturn("invalid-id");
        
        // Simulating that the subject does not exist
        when(subjectRepository.existsById("invalid-id")).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> lessonService.createLesson(dto));
        // Make sure that save was NEVER called
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    // --- PARAMETERIZED TEST (Different Content Types) ---
    @ParameterizedTest
    @ValueSource(strings = {"TEXT", "VIDEO", "MIXED"})
    void testCreateLesson_WithDifferentContentTypes(String contentType) {
        // Arrange
        LessonCreateDTO dto = mock(LessonCreateDTO.class);
        when(dto.getTitle()).thenReturn("Test Title");
        when(dto.getContent()).thenReturn("Test content");
        when(dto.getSubjectId()).thenReturn("sub-1");
        when(dto.getContentType()).thenReturn(contentType);
        
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson result = lessonService.createLesson(dto);

        // Assert
        assertEquals(contentType, result.getContentType());
    }

    // --- UPDATE TESTS ---
    
    @Test
    void testUpdateLesson_Success() {
        // Arrange
        LessonCreateDTO dto = mock(LessonCreateDTO.class);
        when(dto.getTitle()).thenReturn("New Title");
        // Update without subject change (subjectId is null in DTO)
        when(dto.getSubjectId()).thenReturn(null);
        
        Lesson existingLesson = new Lesson("Old Title", "Content", "sub-1");
        
        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", dto);

        // Assert
        assertEquals("New Title", updated.getTitle());
    }

    @Test
    void testUpdateLesson_NotFound_ThrowsException() {
        LessonCreateDTO dto = mock(LessonCreateDTO.class);
        when(lessonRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> lessonService.updateLesson("unknown", dto));
    }

    // --- DELETE TESTS ---

    @Test
    void testDeleteLesson_Success() {
        when(lessonRepository.existsById("lesson-1")).thenReturn(true);
        
        assertDoesNotThrow(() -> lessonService.deleteLesson("lesson-1"));
        
        verify(lessonRepository, times(1)).deleteById("lesson-1");
    }

    @Test
    void testDeleteLesson_NotFound_ThrowsException() {
        when(lessonRepository.existsById("unknown")).thenReturn(false);
        
        assertThrows(NoSuchElementException.class, () -> lessonService.deleteLesson("unknown"));
    }
}