package com.app.movie.Controllers;

import com.app.movie.DTO.reservation.ReservationCreateDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

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
}
