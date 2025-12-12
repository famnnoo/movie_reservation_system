package com.app.movie.Service;


import com.app.movie.DTO.AvailableSeatsDTO;
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

    public AvailableSeatsDTO getAvailableSeats(Long movieId) {

        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // 1. Generate all seats based on totalSeats
        List<String> allSeats = generateSeats(movie.getTotalSeats());

        // 2. Get reserved seats
        List<String> reserved = reservationRepository.findByMovieId(movieId).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .toList();

        // 3. Remove reserved from all seats
        List<String> available = allSeats.stream()
                .filter(seat -> !reserved.contains(seat))
                .toList();

        return new AvailableSeatsDTO(movieId, available);
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
