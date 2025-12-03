package ch.zhaw.stellarcompass.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.stellarcompass.model.Lesson;
import java.util.List;

public interface LessonRepository extends MongoRepository<Lesson, String> {
    // Find alle Lessons that belong to a Subject
    List<Lesson> findBySubjectId(String subjectId);
}