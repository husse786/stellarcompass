package ch.zhaw.stellarcompass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String bio;
    private String avatarUrl;

    // Constructor for easier testing
    public UserUpdateDTO(String name, String bio, String avatarUrl) {
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
}