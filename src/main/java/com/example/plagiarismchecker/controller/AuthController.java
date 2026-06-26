package com.example.plagiarismchecker.controller;

import com.example.plagiarismchecker.dto.AuthRequest;
import com.example.plagiarismchecker.dto.AuthResponse;
import com.example.plagiarismchecker.model.User;
import com.example.plagiarismchecker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest request) {

        if (request.getName() == null || request.getName().trim().isEmpty() ||
                request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "All fields are required", null, null, null));
        }

        String email = request.getEmail().trim().toLowerCase();

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Email already registered", null, null, null));
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(request.getPassword().trim());

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                new AuthResponse(
                        true,
                        "Signup successful",
                        savedUser.getId(),
                        savedUser.getName(),
                        savedUser.getEmail()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Email and password are required", null, null, null));
        }

        String email = request.getEmail().trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "User not found", null, null, null));
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword().trim())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid password", null, null, null));
        }

        return ResponseEntity.ok(
                new AuthResponse(
                        true,
                        "Login successful",
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        );
    }
}