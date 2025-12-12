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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(
            @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.register(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
}
