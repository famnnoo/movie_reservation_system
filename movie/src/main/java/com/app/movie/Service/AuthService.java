package com.app.movie.Service;

import com.app.movie.DTO.auth.LoginRequestDTO;
import com.app.movie.DTO.auth.LoginResponseDTO;
import com.app.movie.DTO.auth.RegistrationResponseDTO;
import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.Models.RefreshToken;
import com.app.movie.Models.Role;
import com.app.movie.Models.User;
import com.app.movie.Repositories.RefreshTokenRepository;
import com.app.movie.Repositories.RoleRepository;
import com.app.movie.Repositories.UserRepository;
import com.app.movie.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public RegistrationResponseDTO registerUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
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
                .map(r -> "ROLE_" + r.getName()) // Add ROLE_ prefix
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getEmail(), roles);
        String refreshToken = createRefreshToken(user);

        return new RegistrationResponseDTO(user.getId(), user.getName(), user.getEmail(), token, refreshToken);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        Set<String> roles = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getEmail(), roles);
        String refreshToken = createRefreshToken(user);

        return new LoginResponseDTO(user.getId(), user.getName(), user.getEmail(), token, refreshToken);
    }

    @Transactional
    public String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        // Create a new token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }


    public String refreshJwt(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        Set<String> roles = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .collect(Collectors.toSet());
        return jwtTokenProvider.generateToken(user.getEmail(), roles);
    }
}

