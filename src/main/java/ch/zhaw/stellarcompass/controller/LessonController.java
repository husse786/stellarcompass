package ch.zhaw.stellarcompass.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.service.LessonService;
import ch.zhaw.stellarcompass.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lesson")
public class LessonController {

    @Autowired
    private LessonService lessonService;
    @Autowired
    private UserService userService;

    // Create lesson: only Admins and Mentors
    @PostMapping
    public ResponseEntity<Lesson> createLesson(@Valid @RequestBody LessonCreateDTO dto) { //validation added
        if(!userService.userHasRole("ADMIN") && !userService.userHasRole("MENTOR")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Lesson createdLesson = lessonService.createLesson(dto);
        // Location header for Rest confrom - best practice
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLesson.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdLesson);
    }

    // Read lessons: all authenticated users
    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return new ResponseEntity<>(lessonService.getAllLessons(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable String id) {
        // Consistency: We use Global Exception Handler for Not Found exceptions
        Lesson lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    // Get lessons by subject ID
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Lesson>> getLessonsBySubject(@PathVariable String subjectId) {
        return new ResponseEntity<>(lessonService.getLessonsBySubject(subjectId), HttpStatus.OK);
    }

    // Update lesson which has valid id: only Admins and Mentors
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable String id, @Valid @RequestBody LessonCreateDTO dto) { //validation added
        if(!userService.userHasRole("ADMIN") && !userService.userHasRole("MENTOR")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Lesson updatedLesson = lessonService.updateLesson(id, dto);
        return new ResponseEntity<>(updatedLesson, HttpStatus.OK);
    }
    // Delete lesson by id: only Admins
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        if(!userService.userHasRole("ADMIN")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        lessonService.deleteLesson(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}