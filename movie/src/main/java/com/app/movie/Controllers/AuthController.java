package com.app.movie.Controllers;

import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.DTO.auth.RegistrationResponseDTO;
import com.app.movie.DTO.auth.LoginRequestDTO;
import com.app.movie.DTO.auth.LoginResponseDTO;
import com.app.movie.Models.Role;
import com.app.movie.Models.User;
import com.app.movie.Repositories.UserRepository;
import com.app.movie.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.registerUser(userDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newJwt = authService.refreshJwt(refreshToken);
        return ResponseEntity.ok(Map.of("token", newJwt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Set<String> roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    return ResponseEntity.ok(Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "roles", roles
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("error", "User not found")));
    }
}

