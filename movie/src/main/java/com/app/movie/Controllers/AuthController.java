package com.app.movie.Controllers;

import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.DTO.auth.RegistrationResponseDTO;
import com.app.movie.DTO.auth.LoginRequestDTO;
import com.app.movie.DTO.auth.LoginResponseDTO;
import com.app.movie.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}

