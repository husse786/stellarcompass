package ch.zhaw.stellarcompass.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreateDTO userDTO) {
        // Here we validate and map the DTO to the User entity
        User user = new User(userDTO.getEmail(), userDTO.getName(), userDTO.getRole());
        if (userDTO.getAuth0Id() != null) {
            user.setAuth0Id(userDTO.getAuth0Id());
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
}