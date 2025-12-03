package ch.zhaw.stellarcompass.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.zhaw.stellarcompass.dto.LessonCreateDTO;
import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.repository.LessonRepository;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

@Service
@Transactional // Ensure that DB operations are transactional and consistent
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // CREATE
    public Lesson createLesson(LessonCreateDTO dto) {
        if (!subjectRepository.existsById(dto.getSubjectId())) {
            throw new NoSuchElementException("Subject mit ID " + dto.getSubjectId() + " nicht gefunden");
        }

        Lesson lesson = new Lesson(dto.getTitle(), dto.getContent(), dto.getSubjectId());
        lesson.setVideoUrl(dto.getVideoUrl());
        lesson.setContentType(dto.getContentType());
        
        return lessonRepository.save(lesson);
    }

    // READ
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson getLessonById(String id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lesson with ID " + id + " not found"));
    }

    public List<Lesson> getLessonsBySubject(String subjectId) {
        return lessonRepository.findBySubjectId(subjectId);
    }

    // UPDATE
    public Lesson updateLesson(String id, LessonCreateDTO dto) {
        // Validate if Subject exists when updating SubjectId
        if (dto.getSubjectId() != null && !subjectRepository.existsById(dto.getSubjectId())) {
            throw new NoSuchElementException("Subject with ID " + dto.getSubjectId() + " not found");
        }

        return lessonRepository.findById(id).map(lesson -> {
            // Null-Safety: Only update fields that are provided in the DTO
            if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
                lesson.setTitle(dto.getTitle());
            }
            if (dto.getContent() != null && !dto.getContent().isBlank()) {
                lesson.setContent(dto.getContent());
            }
            if (dto.getVideoUrl() != null) { // VideoUrl can be empty, but do not overwrite with null if not intended
                lesson.setVideoUrl(dto.getVideoUrl());
            }
            if (dto.getContentType() != null) {
                lesson.setContentType(dto.getContentType());
            }
            if (dto.getSubjectId() != null) {
                lesson.setSubjectId(dto.getSubjectId());
            }
            return lessonRepository.save(lesson);
        }).orElseThrow(() -> new NoSuchElementException("Lesson with ID " + id + " not found"));
    }

    // DELETE
    // Delete optimised (save a DB call if not necessary - if we want exception we use findById first)
    public void deleteLesson(String id) {
       // Direct attempt or check - here stay with the check for the explicit 404 message
       if (!lessonRepository.existsById(id)) {
           throw new NoSuchElementException("Lesson with ID " + id + " not found");
       }
       lessonRepository.deleteById(id);
    }
}