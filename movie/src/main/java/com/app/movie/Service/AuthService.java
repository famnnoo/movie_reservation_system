package com.app.movie.Service;

import com.app.movie.DTO.auth.LoginRequestDTO;
import com.app.movie.DTO.auth.LoginResponseDTO;
import com.app.movie.DTO.auth.RegistrationResponseDTO;
import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.Models.Role;
import com.app.movie.Models.User;
import com.app.movie.Repositories.RoleRepository;
import com.app.movie.Repositories.UserRepository;
import com.app.movie.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public RegistrationResponseDTO register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        String token = jwtTokenProvider.generateToken(user.getEmail(), roles);

        return new RegistrationResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getEmail(), roles);

        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }
}
