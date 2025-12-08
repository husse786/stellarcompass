package ch.zhaw.stellarcompass.model;

import java.time.LocalDate; // For joinDate

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@RequiredArgsConstructor // Lombok annotation to generate a constructor for final fields
@Data // Lombok annotation to generate getters, setters, toString, etc.
@Document("users") // MongoDB collection name
public class User {
    @Id
    private String id; // MongoDB document ID

    @NonNull
    @Indexed(unique = true) // Ensure Emails is unique in the database
    private String email;

    @NonNull
    private String name;

    @NonNull
    private UserRole role; // User role (e.g., ADMIN, USER)
    
    private String auth0Id; // Connection to Auth0 Login (it will important later on - not now)

    private String mentorId; // ID of the mentor user (relevant for students)

    private String bio; // User biography or description
    private String avatarUrl; // URL to the user's avatar image
    private String currentSchoolClassId; // ID of the current classs (Progress tracking)
    private LocalDate joinDate; // Date when the user joined the platform

}
