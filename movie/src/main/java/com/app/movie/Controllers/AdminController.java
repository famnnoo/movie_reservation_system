package com.app.movie.Controllers;

import com.app.movie.DTO.UpdateUserDTO;
import com.app.movie.DTO.UserResponseDTO;
import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ==================== User Management ====================

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PostMapping("/users")
    public UserResponseDTO createUser(@RequestBody UserDTO dto) {
        return adminService.createUser(dto);
    }

    @PutMapping("/users/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        return adminService.updateUser(id, dto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/toggle-admin")
    public UserResponseDTO toggleAdminRole(@PathVariable Long id) {
        return adminService.toggleAdminRole(id);
    }

    // ==================== Reservation Management ====================

    @GetMapping("/reservations")
    public List<ReservationResponseDTO> getAllReservations() {
        return adminService.getAllReservations();
    }

    @GetMapping("/users/{userId}/reservations")
    public List<ReservationResponseDTO> getUserReservations(@PathVariable Long userId) {
        return adminService.getUserReservations(userId);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        adminService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }
}
