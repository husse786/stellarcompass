package ch.zhaw.stellarcompass.dto;

import ch.zhaw.stellarcompass.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserCreateDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    private String auth0Id;
    //mentorId ist optional
    private String mentorId;
}
