package com.app.movie.DTO.reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCreateDTO {

    @NotNull
    private Long displayTimeId;

    @NotEmpty
    private Set<String> seatNumbers;
}
