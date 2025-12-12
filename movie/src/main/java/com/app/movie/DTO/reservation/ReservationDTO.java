package com.app.movie.DTO.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private Long movieId;
    @NotNull
    private Set<String> seatNumbers;
    private LocalDateTime reservationDate;
}

