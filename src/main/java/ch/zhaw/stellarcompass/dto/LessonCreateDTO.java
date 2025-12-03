package ch.zhaw.stellarcompass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;



@NoArgsConstructor
@Getter
public class LessonCreateDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Content is required")
    private String content;
    private String videoUrl;
    private String contentType; // e.g. "TEXT", "VIDEO"
    @NotBlank(message = "Subject ID is required")
    private String subjectId;   // Important: For linking to the subject
}