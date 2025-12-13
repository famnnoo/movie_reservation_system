package com.app.movie.DTO.reservation;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int durationMinutes;
    private LocalDate releaseDate;
    private int totalSeats;
    private String imagePath;
}