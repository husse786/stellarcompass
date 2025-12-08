package ch.zhaw.stellarcompass.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.stellarcompass.dto.UserCreateDTO;
import ch.zhaw.stellarcompass.dto.UserUpdateDTO;
import ch.zhaw.stellarcompass.model.User;
import ch.zhaw.stellarcompass.repository.UserRepository;
import ch.zhaw.stellarcompass.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService; // Service layer for business logic and data access

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO userDTO) {
        User createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Update user: only Admins or Mentor or the user himself
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserCreateDTO userDTO) {
        if(!userService.userHasRole("ADMIN") && !userService.userHasRole("MENTOR")){
            String email = userService.getEmail();
            Optional<User> userOpt = userService.getUserById(id);
            if(userOpt.isEmpty() || !userOpt.get().getEmail().equals(email)){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        User updatedUser = userService.updateUser(id, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if(!userService.userHasRole("ADMIN")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content ist Standard für Delete
    }
    // Get profile of logged-in user
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile( @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if(email == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(email)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
    // Update profile of logged-in user by user himself or admin
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt, 
            @RequestBody UserUpdateDTO updateDTO) {
        
        // Extract email from token
        // Using the logic that is also used in the service (or the claim directly)
        String email = jwt.getClaimAsString("email");
        
        if (email == null) {
            // Fallback falls email im Token anders heisst (z.B. custom claim)
            // Fallback if email is named differently in the token (e.g., custom claim)
            // Adjust this according to Auth0 settings if needed.
            System.out.println("Email claim not found in JWT token.");
            return ResponseEntity.badRequest().build();
        }

        User updatedUser = userService.updateUserProfile(email, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

}