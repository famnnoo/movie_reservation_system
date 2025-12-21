package com.app.movie.Service;


import com.app.movie.DTO.AvailableSeatsDTO;
import com.app.movie.DTO.SeatDTO;
import com.app.movie.DTO.SeatDTO.SeatType;
import com.app.movie.Models.ReservationSeatNumber;
import com.app.movie.Repositories.DisplayTimeRepository;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final MovieRepository movieRepository;
    private final ReservationRepository reservationRepository;
    private final DisplayTimeRepository displayTimeRepository;

    // Standard layout: 10 rows (A-J) x 15 seats per row
    private static final int ROWS = 10;
    private static final int SEATS_PER_ROW = 15;

    public AvailableSeatsDTO getAvailableSeats(Long displayTimeId) {
        var displayTime = displayTimeRepository.findById(displayTimeId)
                .orElseThrow(() -> new RuntimeException("DisplayTime not found"));

        // Get all reserved seat numbers for this display time
        Set<String> reservedSeatNumbers = reservationRepository.findByDisplayTimeId(displayTimeId).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .map(ReservationSeatNumber::getSeatNumbers)
                .collect(Collectors.toSet());

        // Generate all seats with types and reservation status
        List<SeatDTO> seats = generateSeatsWithTypes(reservedSeatNumbers);

        return new AvailableSeatsDTO(displayTime.getMovie().getId(), seats, ROWS, SEATS_PER_ROW);
    }

    private List<SeatDTO> generateSeatsWithTypes(Set<String> reservedSeatNumbers) {
        List<SeatDTO> seats = new ArrayList<>();

        for (int r = 0; r < ROWS; r++) {
            char rowLetter = (char)('A' + r);

            for (int seatNum = 1; seatNum <= SEATS_PER_ROW; seatNum++) {
                String seatNumber = rowLetter + String.valueOf(seatNum);
                SeatType type = determineSeatType(r, seatNum);
                boolean reserved = reservedSeatNumbers.contains(seatNumber);

                seats.add(SeatDTO.builder()
                        .seatNumber(seatNumber)
                        .type(type)
                        .reserved(reserved)
                        .build());
            }
        }

        return seats;
    }

    /**
     * Determine seat type based on position:
     * - VIP: First 2 rows (A-B), middle seats (5-11)
     * - Premium: Rows C-E, middle seats (4-12)
     * - Regular: Everything else
     */
    private SeatType determineSeatType(int rowIndex, int seatNum) {
        // VIP: First 2 rows, center seats
        if (rowIndex < 2 && seatNum >= 5 && seatNum <= 11) {
            return SeatType.VIP;
        }

        // Premium: Rows C-E (index 2-4), good center seats
        if (rowIndex >= 2 && rowIndex <= 4 && seatNum >= 4 && seatNum <= 12) {
            return SeatType.PREMIUM;
        }

        // Everything else is regular
        return SeatType.REGULAR;
    }
}
