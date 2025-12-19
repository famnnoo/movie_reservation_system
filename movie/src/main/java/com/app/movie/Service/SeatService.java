package com.app.movie.Service;


import com.app.movie.DTO.AvailableSeatsDTO;
import com.app.movie.Models.ReservationSeatNumber;
import com.app.movie.Repositories.DisplayTimeRepository;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final MovieRepository movieRepository;
    private final ReservationRepository reservationRepository;
    private final DisplayTimeRepository displayTimeRepository;

    public AvailableSeatsDTO getAvailableSeats(Long displayTimeId) {
        var displayTime = displayTimeRepository.findById(displayTimeId)
                .orElseThrow(() -> new RuntimeException("DisplayTime not found"));

        int totalSeats = displayTime.getMovie().getTotalSeats();
        List<String> allSeats = generateSeats(totalSeats);

        List<String> reservedSeats = reservationRepository.findByDisplayTimeId(displayTimeId).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .map(ReservationSeatNumber::getSeatNumbers)
                .toList();

        return new AvailableSeatsDTO(displayTime.getMovie().getId(), allSeats, reservedSeats);
    }


    private List<String> generateSeats(int totalSeats) {
        List<String> seats = new java.util.ArrayList<>(totalSeats);

        int seatsPerRow = 20; // Example — adjust if needed
        int rowCount = (int) Math.ceil((double) totalSeats / seatsPerRow);

        for (int r = 0; r < rowCount; r++) {
            char rowLetter = (char)('A' + r);

            for (int seat = 1; seat <= seatsPerRow; seat++) {
                int seatIndex = r * seatsPerRow + seat;
                if (seatIndex > totalSeats) break;
                seats.add(rowLetter + String.valueOf(seat));
            }
        }
        return seats;
    }
}
