package ch.zhaw.stellarcompass.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void testCreateSubject() {
        // Arrange
        SubjectCreateDTO dto = mock(SubjectCreateDTO.class);
        when(dto.getTitle()).thenReturn("Mathematik");
        when(dto.getDescription()).thenReturn("Grundlagen der Algebra");

        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject s = invocation.getArgument(0);
            s.setId("sub-123");
            return s;
        });

        // Act
        Subject result = subjectService.createSubject(dto);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Mathematik", result.getTitle());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }
}