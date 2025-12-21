package com.app.movie.DTO.reservation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String movieGenre;
    private String movieImagePath;
    private Long displayTimeId;
    private LocalDateTime displayTime;
    private Set<String> seats;
    private LocalDateTime reservationDate;
}
