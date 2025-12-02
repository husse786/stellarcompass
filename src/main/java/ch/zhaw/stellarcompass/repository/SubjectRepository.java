package ch.zhaw.stellarcompass.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.stellarcompass.model.Subject;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    //Standard CRUD is provided by MongoRepository
}
