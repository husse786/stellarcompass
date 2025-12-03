package ch.zhaw.stellarcompass.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Document("subjects")
public class Subject {
    @Id
    private String id;
    /**
     * The title of the subject, must be unique. like (Mathematik, Physik, Informatik)
     * @Indexed(unique = true) ensures that the title is unique in the MongoDB collection.
     * this will create an index on the title field to enforce uniqueness. and hinder duplicate entries.
     */
    @NonNull
    @Indexed(unique = true)
    private String title;

    @NonNull
    private String description;
}