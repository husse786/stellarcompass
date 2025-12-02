package ch.zhaw.stellarcompass.dto;

import ch.zhaw.stellarcompass.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserCreateDTO {
    private String email;
    private String name;
    private UserRole role;
    private String auth0Id;
    //mentorId ist optional
    private String mentorId;
}
