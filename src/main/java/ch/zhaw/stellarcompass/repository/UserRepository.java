package ch.zhaw.stellarcompass.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.stellarcompass.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    // Spring Data will automatically generate the quieries based on method names
    Optional<User> findByEmail(String email);
    Optional<User> findByAuth0Id(String auth0Id); // NOSONAR
    
}
