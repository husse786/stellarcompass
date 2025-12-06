package ch.zhaw.stellarcompass.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

@SpringBootTest
@ActiveProfiles("test")
class SubjectServiceTest {

    @Autowired
    private SubjectService subjectService;

    @MockBean
    private SubjectRepository subjectRepository;

    private Subject testMath;
    private Subject testCS;
    private Subject testPhysics;
    private SubjectCreateDTO validDTO;

    @BeforeEach
    void setUp() {
        // Setup test subjects
        testMath = new Subject("Mathematik", "Grundlagen der Algebra");
        testMath.setId("math-123");

        testCS = new Subject("Informatik", "Programmieren mit Java");
        testCS.setId("cs-456");

        testPhysics = new Subject("Physik", "Mechanik und Thermodynamik");
        testPhysics.setId("physics-789");

        // Setup valid DTO
        validDTO = mock(SubjectCreateDTO.class);
        when(validDTO.getTitle()).thenReturn("Test Subject");
        when(validDTO.getDescription()).thenReturn("Test Description");
    }

    // ==================== CREATE Tests ====================

    @Test
    void testCreateSubject_Success() {
        // Arrange
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject s = invocation.getArgument(0);
            s.setId("generated-id");
            return s;
        });

        // Act
        Subject result = subjectService.createSubject(validDTO);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Test Subject", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void testCreateSubject_WithEmptyDescription() {
        // Arrange
        when(validDTO.getDescription()).thenReturn("");
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject result = subjectService.createSubject(validDTO);

        // Assert
        assertEquals("Test Subject", result.getTitle());
        assertEquals("", result.getDescription());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void testCreateSubject_WithLongDescription() {
        // Arrange
        String longDescription = "A".repeat(1000);
        when(validDTO.getDescription()).thenReturn(longDescription);
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject result = subjectService.createSubject(validDTO);

        // Assert
        assertEquals(longDescription, result.getDescription());
    }

    @Test
    void testCreateSubject_WithSpecialCharacters() {
        // Arrange
        when(validDTO.getTitle()).thenReturn("Mathematik & Logik");
        when(validDTO.getDescription()).thenReturn("Grundlagen: Algebra, Geometrie & Analysis!");
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject result = subjectService.createSubject(validDTO);

        // Assert
        assertEquals("Mathematik & Logik", result.getTitle());
        assertTrue(result.getDescription().contains("&"));
        assertTrue(result.getDescription().contains("!"));
    }

    @Test
    void testCreateSubject_DifferentSubjects() {
        // Arrange
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> {
            Subject s = i.getArgument(0);
            s.setId("id-" + s.getTitle());
            return s;
        });

        // Act - Create multiple subjects
        when(validDTO.getTitle()).thenReturn("Mathe");
        Subject math = subjectService.createSubject(validDTO);

        when(validDTO.getTitle()).thenReturn("Physik");
        Subject physics = subjectService.createSubject(validDTO);

        // Assert
        assertNotEquals(math.getId(), physics.getId());
        assertEquals("Mathe", math.getTitle());
        assertEquals("Physik", physics.getTitle());
        verify(subjectRepository, times(2)).save(any(Subject.class));
    }

    // ==================== READ Tests ====================

    @Test
    void testGetAllSubjects_Success() {
        // Arrange
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testMath, testCS, testPhysics));

        // Act
        List<Subject> subjects = subjectService.getAllSubjects();

        // Assert
        assertEquals(3, subjects.size());
        assertEquals("Mathematik", subjects.get(0).getTitle());
        assertEquals("Informatik", subjects.get(1).getTitle());
        assertEquals("Physik", subjects.get(2).getTitle());
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSubjects_EmptyList() {
        // Arrange
        when(subjectRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Subject> subjects = subjectService.getAllSubjects();

        // Assert
        assertTrue(subjects.isEmpty());
        assertEquals(0, subjects.size());
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSubjects_SingleSubject() {
        // Arrange
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testMath));

        // Act
        List<Subject> subjects = subjectService.getAllSubjects();

        // Assert
        assertEquals(1, subjects.size());
        assertEquals("Mathematik", subjects.get(0).getTitle());
    }

    @Test
    void testGetAllSubjects_OrderPreserved() {
        // Arrange - Specific order
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testPhysics, testMath, testCS));

        // Act
        List<Subject> subjects = subjectService.getAllSubjects();

        // Assert - Order should be preserved
        assertEquals("Physik", subjects.get(0).getTitle());
        assertEquals("Mathematik", subjects.get(1).getTitle());
        assertEquals("Informatik", subjects.get(2).getTitle());
    }

    // ==================== UPDATE Tests ====================

    @Test
    void testUpdateSubject_Success() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Updated Title");
        when(updateDTO.getDescription()).thenReturn("Updated Description");

        when(subjectRepository.findById("math-123")).thenReturn(Optional.of(testMath));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject updated = subjectService.updateSubject("math-123", updateDTO);

        // Assert
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals("math-123", updated.getId()); // ID should remain the same
        verify(subjectRepository, times(1)).findById("math-123");
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void testUpdateSubject_OnlyTitle() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("New Title");
        when(updateDTO.getDescription()).thenReturn("Updated Description");

        when(subjectRepository.findById("cs-456")).thenReturn(Optional.of(testCS));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject updated = subjectService.updateSubject("cs-456", updateDTO);

        // Assert
        assertEquals("New Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void testUpdateSubject_OnlyDescription() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Informatik"); // Same title
        when(updateDTO.getDescription()).thenReturn("Neue Beschreibung");

        when(subjectRepository.findById("cs-456")).thenReturn(Optional.of(testCS));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject updated = subjectService.updateSubject("cs-456", updateDTO);

        // Assert
        assertEquals("Informatik", updated.getTitle());
        assertEquals("Neue Beschreibung", updated.getDescription());
    }

    @Test
    void testUpdateSubject_NotFound_ThrowsException() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Title");
        when(updateDTO.getDescription()).thenReturn("Description");

        when(subjectRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> subjectService.updateSubject("unknown", updateDTO)
        );

        assertTrue(exception.getMessage().contains("The Subject you are looking for, is not found"));
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void testUpdateSubject_NullId_ThrowsException() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(subjectRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> subjectService.updateSubject(null, updateDTO));
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void testUpdateSubject_EmptyFields() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("");
        when(updateDTO.getDescription()).thenReturn("");

        when(subjectRepository.findById("math-123")).thenReturn(Optional.of(testMath));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject updated = subjectService.updateSubject("math-123", updateDTO);

        // Assert - Empty strings are allowed
        assertEquals("", updated.getTitle());
        assertEquals("", updated.getDescription());
    }

    @Test
    void testUpdateSubject_WithSpecialCharacters() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Mathe & Physik");
        when(updateDTO.getDescription()).thenReturn("Beschreibung mit Sonderzeichen: äöü ß € @");

        when(subjectRepository.findById("math-123")).thenReturn(Optional.of(testMath));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Subject updated = subjectService.updateSubject("math-123", updateDTO);

        // Assert
        assertEquals("Mathe & Physik", updated.getTitle());
        assertTrue(updated.getDescription().contains("ä"));
        assertTrue(updated.getDescription().contains("€"));
    }

    // ==================== DELETE Tests ====================

    @Test
    void testDeleteSubject_Success() {
        // Arrange
        doNothing().when(subjectRepository).deleteById("math-123");

        // Act
        assertDoesNotThrow(() -> subjectService.deleteSubject("math-123"));

        // Assert
        verify(subjectRepository, times(1)).deleteById("math-123");
    }

    @Test
    void testDeleteSubject_NullId() {
        // Arrange
        doNothing().when(subjectRepository).deleteById(null);

        // Act
        assertDoesNotThrow(() -> subjectService.deleteSubject(null));

        // Assert
        verify(subjectRepository, times(1)).deleteById(null);
    }

    @Test
    void testDeleteSubject_NonExistentId() {
        // Arrange - MongoDB deleteById doesn't throw exception if ID doesn't exist
        doNothing().when(subjectRepository).deleteById("nonexistent");

        // Act
        assertDoesNotThrow(() -> subjectService.deleteSubject("nonexistent"));

        // Assert
        verify(subjectRepository, times(1)).deleteById("nonexistent");
    }

    @Test
    void testDeleteSubject_MultipleDeletes() {
        // Arrange
        doNothing().when(subjectRepository).deleteById(anyString());

        // Act
        subjectService.deleteSubject("math-123");
        subjectService.deleteSubject("cs-456");
        subjectService.deleteSubject("physics-789");

        // Assert
        verify(subjectRepository, times(3)).deleteById(anyString());
        verify(subjectRepository, times(1)).deleteById("math-123");
        verify(subjectRepository, times(1)).deleteById("cs-456");
        verify(subjectRepository, times(1)).deleteById("physics-789");
    }

    // ==================== Integration Tests ====================

    @Test
    void testCreateAndRetrieve_Integration() {
        // Arrange
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject saved = invocation.getArgument(0);
            saved.setId("new-subject-id");
            return saved;
        });
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testMath));

        // Act
        Subject created = subjectService.createSubject(validDTO);
        List<Subject> all = subjectService.getAllSubjects();

        // Assert
        assertNotNull(created);
        assertFalse(all.isEmpty());
        verify(subjectRepository, times(1)).save(any(Subject.class));
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAndRetrieve_Integration() {
        // Arrange
        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Updated");
        when(updateDTO.getDescription()).thenReturn("New Description");

        when(subjectRepository.findById("math-123")).thenReturn(Optional.of(testMath));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(i -> i.getArgument(0));
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testMath));

        // Act
        Subject updated = subjectService.updateSubject("math-123", updateDTO);
        List<Subject> all = subjectService.getAllSubjects();

        // Assert
        assertEquals("Updated", updated.getTitle());
        assertFalse(all.isEmpty());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void testCreateUpdateDelete_FullLifecycle() {
        // Arrange
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject s = invocation.getArgument(0);
            s.setId("lifecycle-id");
            return s;
        });

        SubjectCreateDTO updateDTO = mock(SubjectCreateDTO.class);
        when(updateDTO.getTitle()).thenReturn("Updated Subject");
        when(updateDTO.getDescription()).thenReturn("Updated Description");

        Subject createdSubject = new Subject("Test Subject", "Test Description");
        createdSubject.setId("lifecycle-id");
        
        when(subjectRepository.findById("lifecycle-id")).thenReturn(Optional.of(createdSubject));
        doNothing().when(subjectRepository).deleteById("lifecycle-id");

        // Act
        Subject created = subjectService.createSubject(validDTO);
        Subject updated = subjectService.updateSubject("lifecycle-id", updateDTO);
        subjectService.deleteSubject("lifecycle-id");

        // Assert
        assertNotNull(created.getId());
        assertEquals("Updated Subject", updated.getTitle());
        verify(subjectRepository, times(2)).save(any(Subject.class)); // CREATE + UPDATE both save
        verify(subjectRepository, times(1)).deleteById("lifecycle-id");
    }

    // ==================== Edge Cases ====================

@Test
void testCreateSubject_NullTitle_ThrowsException() {
    // Arrange - DTO with null title
    SubjectCreateDTO nullTitleDTO = mock(SubjectCreateDTO.class);
    when(nullTitleDTO.getTitle()).thenReturn(null);
    when(nullTitleDTO.getDescription()).thenReturn("Valid Description");

    // Act & Assert - Should throw NullPointerException due to @NonNull annotation
    assertThrows(NullPointerException.class, () -> {
        subjectService.createSubject(nullTitleDTO);
    }, "Should throw NullPointerException when title is null");
}

@Test
void testCreateSubject_NullDescription_ThrowsException() {
    // Arrange - DTO with null description
    SubjectCreateDTO nullDescDTO = mock(SubjectCreateDTO.class);
    when(nullDescDTO.getTitle()).thenReturn("Valid Title");
    when(nullDescDTO.getDescription()).thenReturn(null);

    // Act & Assert - Should throw NullPointerException due to @NonNull annotation
    assertThrows(NullPointerException.class, () -> {
        subjectService.createSubject(nullDescDTO);
    }, "Should throw NullPointerException when description is null");
}

@Test
void testCreateSubject_BothNull_ThrowsException() {
    // Arrange - DTO with both null
    SubjectCreateDTO nullDTO = mock(SubjectCreateDTO.class);
    when(nullDTO.getTitle()).thenReturn(null);
    when(nullDTO.getDescription()).thenReturn(null);

    // Act & Assert - Should throw NullPointerException due to @NonNull annotation
    assertThrows(NullPointerException.class, () -> {
        subjectService.createSubject(nullDTO);
    }, "Should throw NullPointerException when both fields are null");
}

    @Test
    void testGetAllSubjects_CalledMultipleTimes() {
        // Arrange
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(testMath));

        // Act
        subjectService.getAllSubjects();
        subjectService.getAllSubjects();
        subjectService.getAllSubjects();

        // Assert
        verify(subjectRepository, times(3)).findAll();
    }
}