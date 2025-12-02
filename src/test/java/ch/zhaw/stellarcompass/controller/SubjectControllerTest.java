package ch.zhaw.stellarcompass.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.service.SubjectService;

@SpringBootTest
@AutoConfigureMockMvc
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectService subjectService;

    @Test
    void testCreateSubjectEndpoint() throws Exception {
        Subject mockSubject = new Subject("Informatik", "Programmieren mit Java");
        mockSubject.setId("sub-456");

        when(subjectService.createSubject(any(SubjectCreateDTO.class))).thenReturn(mockSubject);

        String jsonBody = "{\"title\": \"Informatik\", \"description\": \"Programmieren mit Java\"}";

        mockMvc.perform(post("/api/subject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sub-456"))
                .andExpect(jsonPath("$.title").value("Informatik"));
    }

    @Test
    void testGetAllSubjectsEndpoint() throws Exception {
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(new Subject("Mathe", "Zahlen")));

        mockMvc.perform(get("/api/subject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Mathe"));
    }
}