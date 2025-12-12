package com.app.movie.DTO;

public record LoginResponseDTO(
        Long id,
        String name,
        String email,
        String token
) {}