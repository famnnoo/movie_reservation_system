package com.app.movie.DTO.reservation;

import com.app.movie.Models.DisplayTime;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


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
    private List<com.app.movie.DTO.reservation.DisplayTimeResponseDTO> displayTimes;

    private String genre;
    private Set<String> cinemas;
    private Set<String> locations; // Cities where movie is playing
    private Double imdbRating; // IMDb rating


}