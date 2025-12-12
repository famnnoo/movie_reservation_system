package com.app.movie.DTO.auth;

public record LoginResponseDTO(
        Long id,
        String name,
        String email,
        String token,
        String refreshToken
) {}

