package ch.zhaw.stellarcompass.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.stellarcompass.dto.SubjectCreateDTO;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject createSubject(SubjectCreateDTO subjectDTO) {
        Subject subject = new Subject(subjectDTO.getTitle(), subjectDTO.getDescription());
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // Update
    public Subject updateSubject(String id, SubjectCreateDTO dto) {
        return subjectRepository.findById(id).map(subject -> {
            subject.setTitle(dto.getTitle());
            subject.setDescription(dto.getDescription());
            return subjectRepository.save(subject);
        }).orElseThrow(() -> new RuntimeException("The Subject you are looking for, is not found"));
    }

    // Delete
    public void deleteSubject(String id) {
        subjectRepository.deleteById(id);
    }
}