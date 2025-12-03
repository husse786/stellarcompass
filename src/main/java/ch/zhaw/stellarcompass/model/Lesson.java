package ch.zhaw.stellarcompass.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Document("lessons")
public class Lesson {
    @Id
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private String videoUrl;
    private String contentType;

    @NonNull
    private String subjectId; // Foreign key to Subject
}
