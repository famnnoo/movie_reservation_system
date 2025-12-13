package com.app.movie.DTO.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDTO {
    private Long id;
    @NotBlank
    String name;
    @NotBlank
    private String title;
    @NotBlank
    private String description;

    @Min(1)
    private int durationMinutes;

    private LocalDate releaseDate;

    @Min(1)
    private int totalSeats;

    @NotBlank
    private String imagePath;
}
