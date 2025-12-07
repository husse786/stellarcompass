package ch.zhaw.stellarcompass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.service.SubjectService;
import ch.zhaw.stellarcompass.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;
    // CREATE: only Admins and Mentors
    @PostMapping
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody SubjectCreateDTO subjectDTO) {
        if(!userService.userHasRole("ADMIN") && !userService.userHasRole("MENTOR")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Subject createdSubject = subjectService.createSubject(subjectDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }
    // READ: all authenticated users
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return new ResponseEntity<>(subjectService.getAllSubjects(), HttpStatus.OK);
    }

    // UPDATE: only Admins and Mentors
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable String id, @Valid @RequestBody SubjectCreateDTO dto) {
        if(!userService.userHasRole("ADMIN") && !userService.userHasRole("MENTOR")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Subject updatedSubject = subjectService.updateSubject(id, dto);
            return new ResponseEntity<>(updatedSubject, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // DELETE: only Admins
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable String id) {
        if(!userService.userHasRole("ADMIN")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        subjectService.deleteSubject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}