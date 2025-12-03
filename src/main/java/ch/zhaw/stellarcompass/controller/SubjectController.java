package ch.zhaw.stellarcompass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.service.SubjectService;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectCreateDTO subjectDTO) {
        Subject createdSubject = subjectService.createSubject(subjectDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return new ResponseEntity<>(subjectService.getAllSubjects(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable String id, @RequestBody SubjectCreateDTO dto) {
        try {
            Subject updatedSubject = subjectService.updateSubject(id, dto);
            return new ResponseEntity<>(updatedSubject, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable String id) {
        subjectService.deleteSubject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}