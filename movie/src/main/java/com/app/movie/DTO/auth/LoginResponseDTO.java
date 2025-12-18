package com.app.movie.DTO.auth;

import java.util.Set;

public record LoginResponseDTO(
        Long id,
        String name,
        String email,
        String token,
        String refreshToken,
        Set<String> roles
) {}

