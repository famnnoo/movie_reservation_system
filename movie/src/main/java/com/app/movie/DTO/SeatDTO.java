package com.app.movie.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatDTO {
    private String seatNumber;  // e.g., "A1"
    private SeatType type;      // REGULAR, PREMIUM, VIP
    private boolean reserved;   // true if already reserved

    public enum SeatType {
        REGULAR,
        PREMIUM,
        VIP
    }
}

