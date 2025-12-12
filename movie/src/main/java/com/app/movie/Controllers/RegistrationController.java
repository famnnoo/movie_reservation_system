package com.app.movie.Controllers;

import com.app.movie.DTO.auth.UserDTO;

import com.app.movie.Security.JwtTokenProvider;
import com.app.movie.Models.Role;
import com.app.movie.Models.User;
import com.app.movie.Repositories.RoleRepository;
import com.app.movie.Repositories.UserRepository;
import com.app.movie.DTO.auth.RegistrationResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@Validated
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        // Fetch USER role from DB
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found in database"));

        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .passwordHash(passwordEncoder.encode(userDTO.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        // Generate JWT token including roles
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());
        String token = jwtTokenProvider.generateToken(user.getEmail(), roles);

        return ResponseEntity.ok(new RegistrationResponseDTO(user.getId(), user.getName(), user.getEmail(), token));
    }
}
