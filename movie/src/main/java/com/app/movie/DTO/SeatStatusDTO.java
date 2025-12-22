package com.app.movie.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for real-time seat status updates via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatStatusDTO {
    private Long displayTimeId;
    private String seatNumber;
    private SeatStatus status;
    private Long secondsRemaining; // For held seats
    private Long userId; // Who holds/reserved it

    public enum SeatStatus {
        AVAILABLE,
        HELD,      // Temporarily held (with countdown)
        RESERVED   // Permanently reserved
    }
}

