package com.app.movie.Controllers;

import com.app.movie.DTO.reservation.ReservationCreateDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Models.Reservation;
import com.app.movie.Models.User;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final MovieRepository movieRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ReservationResponseDTO createReservation(@RequestBody ReservationCreateDTO dto) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<String> takenSeats = reservationRepository.findByMovieId(dto.getMovieId()).stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        for (String seat : dto.getSeatNumbers()) {
            if (takenSeats.contains(seat)) {
                throw new RuntimeException("Seat " + seat + " is already reserved");
            }
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .movie(movieRepository.findById(dto.getMovieId())
                        .orElseThrow(() -> new RuntimeException("Movie not found")))
                .seatNumbers(dto.getSeatNumbers())
                .reservationDate(LocalDateTime.now())
                .build();

        Reservation saved = reservationRepository.save(reservation);

        return convertToResponseDTO(saved);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<ReservationResponseDTO> getUserReservations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return reservationRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReservationResponseDTO convertToResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .movieId(reservation.getMovie().getId())
                .seatNumbers(reservation.getSeatNumbers())
                .reservationDate(reservation.getReservationDate())
                .build();
    }
}

