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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.repository.LessonRepository;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

@SpringBootTest
@ActiveProfiles("test")
class LessonServiceTest {

    @Autowired
    private LessonService lessonService;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private SubjectRepository subjectRepository;

    private Lesson testLesson1;
    private Lesson testLesson2;
    private LessonCreateDTO validDTO;

    @BeforeEach
    void setUp() {
        // Setup test lessons
        testLesson1 = new Lesson("Intro to Java", "Basic concepts", "sub-1");
        testLesson1.setId("lesson-1");
        testLesson1.setContentType("TEXT");

        testLesson2 = new Lesson("Advanced Java", "Advanced topics", "sub-1");
        testLesson2.setId("lesson-2");
        testLesson2.setContentType("VIDEO");
        testLesson2.setVideoUrl("https://example.com/video.mp4");

        // Setup valid DTO
        validDTO = mock(LessonCreateDTO.class);
        when(validDTO.getTitle()).thenReturn("Test Title");
        when(validDTO.getContent()).thenReturn("Test Content");
        when(validDTO.getSubjectId()).thenReturn("sub-1");
        when(validDTO.getContentType()).thenReturn("TEXT");
    }

    // ==================== CREATE Tests ====================

    @Test
    void testCreateLesson_Success() {
        // Arrange
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson saved = invocation.getArgument(0);
            saved.setId("generated-id");
            return saved;
        });

        // Act
        Lesson created = lessonService.createLesson(validDTO);

        // Assert
        assertNotNull(created);
        assertEquals("Test Title", created.getTitle());
        assertEquals("Test Content", created.getContent());
        assertEquals("sub-1", created.getSubjectId());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
        verify(subjectRepository, times(1)).existsById("sub-1");
    }

    @Test
    void testCreateLesson_WithVideoUrl() {
        // Arrange
        when(validDTO.getVideoUrl()).thenReturn("https://youtube.com/watch?v=123");
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson created = lessonService.createLesson(validDTO);

        // Assert
        assertEquals("https://youtube.com/watch?v=123", created.getVideoUrl());
    }

    @Test
    void testCreateLesson_SubjectNotFound_ThrowsException() {
        // Arrange
        when(subjectRepository.existsById("sub-1")).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> lessonService.createLesson(validDTO)
        );
        
        assertTrue(exception.getMessage().contains("Subject mit ID sub-1 nicht gefunden"));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void testCreateLesson_NullSubjectId_ThrowsException() {
        // Arrange
        when(validDTO.getSubjectId()).thenReturn(null);
        when(subjectRepository.existsById(null)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> lessonService.createLesson(validDTO));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"TEXT", "VIDEO", "MIXED"})
    void testCreateLesson_WithDifferentContentTypes(String contentType) {
        // Arrange
        when(validDTO.getContentType()).thenReturn(contentType);
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson result = lessonService.createLesson(validDTO);

        // Assert
        assertEquals(contentType, result.getContentType());
    }

    // ==================== READ Tests ====================

    @Test
    void testGetAllLessons_Success() {
        // Arrange
        when(lessonRepository.findAll()).thenReturn(Arrays.asList(testLesson1, testLesson2));

        // Act
        List<Lesson> lessons = lessonService.getAllLessons();

        // Assert
        assertEquals(2, lessons.size());
        assertEquals("Intro to Java", lessons.get(0).getTitle());
        assertEquals("Advanced Java", lessons.get(1).getTitle());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    void testGetAllLessons_EmptyList() {
        // Arrange
        when(lessonRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Lesson> lessons = lessonService.getAllLessons();

        // Assert
        assertTrue(lessons.isEmpty());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    void testGetLessonById_Success() {
        // Arrange
        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));

        // Act
        Lesson found = lessonService.getLessonById("lesson-1");

        // Assert
        assertNotNull(found);
        assertEquals("lesson-1", found.getId());
        assertEquals("Intro to Java", found.getTitle());
        verify(lessonRepository, times(1)).findById("lesson-1");
    }

    @Test
    void testGetLessonById_NotFound_ThrowsException() {
        // Arrange
        when(lessonRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> lessonService.getLessonById("unknown")
        );
        
        assertTrue(exception.getMessage().contains("Lesson with ID unknown not found"));
    }

    @Test
    void testGetLessonsBySubject_Success() {
        // Arrange
        when(lessonRepository.findBySubjectId("sub-1"))
            .thenReturn(Arrays.asList(testLesson1, testLesson2));

        // Act
        List<Lesson> lessons = lessonService.getLessonsBySubject("sub-1");

        // Assert
        assertEquals(2, lessons.size());
        assertEquals("sub-1", lessons.get(0).getSubjectId());
        assertEquals("sub-1", lessons.get(1).getSubjectId());
        verify(lessonRepository, times(1)).findBySubjectId("sub-1");
    }

    @Test
    void testGetLessonsBySubject_EmptyList() {
        // Arrange
        when(lessonRepository.findBySubjectId("sub-999")).thenReturn(Collections.emptyList());

        // Act
        List<Lesson> lessons = lessonService.getLessonsBySubject("sub-999");

        // Assert
        assertTrue(lessons.isEmpty());
        verify(lessonRepository, times(1)).findBySubjectId("sub-999");
    }

    @Test
    void testGetLessonsBySubject_NullSubjectId() {
        // Arrange
        when(lessonRepository.findBySubjectId(null)).thenReturn(Collections.emptyList());

        // Act
        List<Lesson> lessons = lessonService.getLessonsBySubject(null);

        // Assert
        assertTrue(lessons.isEmpty());
    }

    // ==================== UPDATE Tests ====================

    @Test
    void testUpdateLesson_AllFields_Success() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Updated Title");
        when(updateDTO.getContent()).thenReturn("Updated Content");
        when(updateDTO.getSubjectId()).thenReturn("sub-2");
        when(updateDTO.getVideoUrl()).thenReturn("https://new-url.com");
        when(updateDTO.getContentType()).thenReturn("VIDEO");

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));
        when(subjectRepository.existsById("sub-2")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", updateDTO);

        // Assert
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Content", updated.getContent());
        assertEquals("sub-2", updated.getSubjectId());
        assertEquals("https://new-url.com", updated.getVideoUrl());
        assertEquals("VIDEO", updated.getContentType());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_PartialUpdate_OnlyTitle() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("New Title");
        when(updateDTO.getContent()).thenReturn(null); // Not updating
        when(updateDTO.getSubjectId()).thenReturn(null); // Not updating

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", updateDTO);

        // Assert
        assertEquals("New Title", updated.getTitle());
        assertEquals("Basic concepts", updated.getContent()); // Unchanged
        assertEquals("sub-1", updated.getSubjectId()); // Unchanged
    }

    @Test
    void testUpdateLesson_BlankTitle_NotUpdated() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("   "); // Blank
        when(updateDTO.getContent()).thenReturn("New Content");

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", updateDTO);

        // Assert
        assertEquals("Intro to Java", updated.getTitle()); // Not changed (blank)
        assertEquals("New Content", updated.getContent()); // Changed
    }

    @Test
    void testUpdateLesson_NewSubjectNotFound_ThrowsException() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("New Title");
        when(updateDTO.getSubjectId()).thenReturn("invalid-subject");

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));
        when(subjectRepository.existsById("invalid-subject")).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> lessonService.updateLesson("lesson-1", updateDTO)
        );
        
        assertTrue(exception.getMessage().contains("Subject with ID invalid-subject not found"));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_LessonNotFound_ThrowsException() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(lessonRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> lessonService.updateLesson("unknown", updateDTO)
        );
        
        assertTrue(exception.getMessage().contains("Lesson with ID unknown not found"));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_EmptyVideoUrl() {
        // Arrange
        LessonCreateDTO updateDTO = mock(LessonCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Title");
        when(updateDTO.getVideoUrl()).thenReturn(""); // Empty string

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(testLesson1));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", updateDTO);

        // Assert - Empty string is allowed (not null)
        assertEquals("", updated.getVideoUrl());
    }

    // ==================== DELETE Tests ====================

    @Test
    void testDeleteLesson_Success() {
        // Arrange
        when(lessonRepository.existsById("lesson-1")).thenReturn(true);
        doNothing().when(lessonRepository).deleteById("lesson-1");

        // Act
        assertDoesNotThrow(() -> lessonService.deleteLesson("lesson-1"));

        // Assert
        verify(lessonRepository, times(1)).existsById("lesson-1");
        verify(lessonRepository, times(1)).deleteById("lesson-1");
    }

    @Test
    void testDeleteLesson_NotFound_ThrowsException() {
        // Arrange
        when(lessonRepository.existsById("unknown")).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> lessonService.deleteLesson("unknown")
        );
        
        assertTrue(exception.getMessage().contains("Lesson with ID unknown not found"));
        verify(lessonRepository, never()).deleteById(anyString());
    }

    @Test
    void testDeleteLesson_NullId_ThrowsException() {
        // Arrange
        when(lessonRepository.existsById(null)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> lessonService.deleteLesson(null));
        verify(lessonRepository, never()).deleteById(anyString());
    }

    // ==================== Integration/Edge Cases ====================

    @Test
    void testCreateAndRetrieve_Integration() {
        // Arrange
        when(subjectRepository.existsById("sub-1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson saved = invocation.getArgument(0);
            saved.setId("new-lesson-id");
            return saved;
        });
        when(lessonRepository.findById("new-lesson-id")).thenReturn(Optional.of(testLesson1));

        // Act
        Lesson created = lessonService.createLesson(validDTO);
        Lesson retrieved = lessonService.getLessonById("new-lesson-id");

        // Assert
        assertNotNull(created);
        assertNotNull(retrieved);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
        verify(lessonRepository, times(1)).findById("new-lesson-id");
    }

    @Test
    void testUpdateLesson_NoChanges() {
        // Arrange - All DTO fields are null or blank
        LessonCreateDTO noChangeDTO = mock(LessonCreateDTO.class);
        when(noChangeDTO.getTitle()).thenReturn(null);
        when(noChangeDTO.getContent()).thenReturn(null);
        when(noChangeDTO.getSubjectId()).thenReturn(null);
        when(noChangeDTO.getVideoUrl()).thenReturn(null);
        when(noChangeDTO.getContentType()).thenReturn(null);

        Lesson originalLesson = new Lesson("Original", "Content", "sub-1");
        originalLesson.setContentType("TEXT");

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(originalLesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lesson updated = lessonService.updateLesson("lesson-1", noChangeDTO);

        // Assert - Nothing changed
        assertEquals("Original", updated.getTitle());
        assertEquals("Content", updated.getContent());
        assertEquals("sub-1", updated.getSubjectId());
        assertEquals("TEXT", updated.getContentType());
    }
}