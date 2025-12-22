package com.app.movie.Service;

import com.app.movie.DTO.reservation.ReservationCreateDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Models.*;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import com.app.movie.Repositories.DisplayTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MovieRepository movieRepository;
    private final DisplayTimeRepository displayTimeRepository;

    /**
     * Create a reservation with proper concurrency control to prevent double-booking.
     * Uses pessimistic locking and database constraints to ensure data integrity under high load.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponseDTO createReservation(ReservationCreateDTO dto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lock the display time row to prevent concurrent modifications
        DisplayTime displayTime = displayTimeRepository.findByIdWithLock(dto.getDisplayTimeId())
                .orElseThrow(() -> new RuntimeException("DisplayTime not found"));

        // Collect all already reserved seats for this movie session
        // This query is protected by the lock on display time
        Set<String> takenSeats = reservationRepository.findByDisplayTimeId(displayTime.getId()).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .map(ReservationSeatNumber::getSeatNumbers)
                .collect(Collectors.toSet());

        // Check if requested seats are available
        for (String seat : dto.getSeatNumbers()) {
            if (takenSeats.contains(seat)) {
                throw new RuntimeException("Seat " + seat + " is already reserved");
            }
        }

        // Create reservation
        Reservation reservation = Reservation.builder()
                .user(user)
                .movie(displayTime.getMovie())
                .displayTime(displayTime)
                .reservationDate(LocalDateTime.now())
                .build();

        // Convert seat strings to ReservationSeatNumber with display time reference
        Set<ReservationSeatNumber> seatEntities = dto.getSeatNumbers().stream()
                .map(seat -> {
                    ReservationSeatNumber rsn = new ReservationSeatNumber();
                    rsn.setSeatNumbers(seat);
                    rsn.setReservation(reservation);
                    rsn.setDisplayTime(displayTime); // Critical: links seat to display time
                    return rsn;
                })
                .collect(Collectors.toSet());

        reservation.setSeatNumbers(seatEntities);

        try {
            // Save reservation with seats
            // Database constraint ensures uniqueness of (display_time_id, seat_numbers)
            Reservation saved = reservationRepository.save(reservation);
            return convertToResponseDTO(saved);
            
        } catch (DataIntegrityViolationException e) {
            // This catches the case where constraint was violated
            // (e.g., two requests tried to book the same seat simultaneously)
            throw new RuntimeException("One or more seats were just booked by another user. Please try different seats.");
        }
    }

    public List<ReservationResponseDTO> getUserReservations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return reservationRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReservationResponseDTO convertToResponseDTO(Reservation reservation) {
        Set<String> seats = reservation.getSeatNumbers().stream()
                .map(ReservationSeatNumber::getSeatNumbers)
                .collect(Collectors.toSet());

        Movie movie = reservation.getMovie();
        DisplayTime displayTime = reservation.getDisplayTime();

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .movieGenre(movie.getGenre())
                .movieImagePath(movie.getImagePath())
                .displayTimeId(displayTime.getId())
                .displayTime(displayTime.getStartTime())
                .seats(seats)
                .reservationDate(reservation.getReservationDate())
                .build();
    }
}
