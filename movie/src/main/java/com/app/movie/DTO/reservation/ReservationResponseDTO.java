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
    private Set<String> seatNumbers;
    private LocalDateTime reservationDate;
}
