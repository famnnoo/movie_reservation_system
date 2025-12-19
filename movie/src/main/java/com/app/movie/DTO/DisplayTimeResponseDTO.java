package com.app.movie.DTO.reservation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DisplayTimeResponseDTO {
    private Long id;
    private LocalDateTime time;
}
