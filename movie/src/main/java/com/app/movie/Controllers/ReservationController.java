package com.app.movie.Controllers;

import com.app.movie.DTO.AvailableSeatsDTO;
import com.app.movie.DTO.reservation.ReservationCreateDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Service.ReservationService;
import com.app.movie.Service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final SeatService seatService;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ReservationResponseDTO createReservation(@RequestBody ReservationCreateDTO dto) {
        return reservationService.createReservation(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<ReservationResponseDTO> getUserReservations() {
        return reservationService.getUserReservations();
    }

    @GetMapping("/seats/{movieId}/{displayTimeId}")
    public AvailableSeatsDTO getSeats(
            @PathVariable Long movieId,
            @PathVariable Long displayTimeId
    ) {
        return seatService.getAvailableSeats(displayTimeId);

    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ReservationResponseDTO create(@RequestBody ReservationCreateDTO dto) {
        return reservationService.createReservation(dto);
    }

}
