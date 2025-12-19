package com.app.movie.Service;

import com.app.movie.DTO.reservation.ReservationCreateDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Models.*;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import com.app.movie.Repositories.DisplayTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public ReservationResponseDTO createReservation(ReservationCreateDTO dto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DisplayTime displayTime = displayTimeRepository.findById(dto.getDisplayTimeId())
                .orElseThrow(() -> new RuntimeException("DisplayTime not found"));

        // Collect all already reserved seats for this movie session
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

        // Convert seat strings to ReservationSeatNumber
        Set<ReservationSeatNumber> seatEntities = dto.getSeatNumbers().stream()
                .map(seat -> {
                    ReservationSeatNumber rsn = new ReservationSeatNumber();
                    rsn.setSeatNumbers(seat);
                    rsn.setReservation(reservation);
                    return rsn;
                })
                .collect(Collectors.toSet());

        reservation.setSeatNumbers(seatEntities);

        // Save reservation with seats
        Reservation saved = reservationRepository.save(reservation);

        return convertToResponseDTO(saved);
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

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .movieId(reservation.getMovie().getId())
                .displayTimeId(reservation.getDisplayTime().getId())
                .seatNumbers(seats)
                .reservationDate(reservation.getReservationDate())
                .build();
    }
}
