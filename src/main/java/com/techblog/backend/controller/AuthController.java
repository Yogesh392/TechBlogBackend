package com.techblog.backend.controller;

import com.techblog.backend.dto.JwtResponse;
import com.techblog.backend.dto.LoginRequest;
import com.techblog.backend.dto.SignUpRequest;
import com.techblog.backend.entity.User;
import com.techblog.backend.entity.UserProfile;
import com.techblog.backend.repository.UserProfileRepository;
import com.techblog.backend.repository.UserRepository;
import com.techblog.backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username is already taken!"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email is already in use!"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String fullName = "";
        if (request.getFirstName() != null) {
            fullName = request.getFirstName();
        }
        if (request.getLastName() != null) {
            fullName += " " + request.getLastName();
        }
        user.setFullName(fullName.trim());

        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Auth test works!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        String identifier = request.getUsername();
        if (identifier.contains("@")) {
            User user = userRepository.findByEmail(identifier).orElse(null);
            if (user != null) {
                identifier = user.getUsername();
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        request.getPassword()));

        String jwt = jwtUtil.generateToken(authentication.getName());

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}