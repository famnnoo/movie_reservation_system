//package com.app.movie.Controllers;
//
//import com.app.movie.DTO.UpdateUserDTO;
//import com.app.movie.DTO.UserResponseDTO;
//import com.app.movie.DTO.auth.UserDTO;
//import com.app.movie.DTO.reservation.MovieDTO;
//import com.app.movie.DTO.reservation.MovieResponseDTO;
//import com.app.movie.DTO.reservation.ReservationResponseDTO;
//import com.app.movie.Service.AdminService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    private final AdminService adminService;
//
//    @GetMapping("/users")
//    public List<UserResponseDTO> getAllUsers() {
//        return adminService.getAllUsers();
//    }
//
//    @PostMapping("/users")
//    public UserResponseDTO createUser(@RequestBody UserDTO dto) {
//        return adminService.createUser(dto);
//    }
//
//    @DeleteMapping("/users/{id}")
//    public void deleteUser(@PathVariable Long id) {
//        adminService.deleteUser(id);
//    }
//
//    @PutMapping("/users/{id}")
//    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
//        return adminService.updateUser(id, dto);
//    }
//
//    @GetMapping("/users/{userId}/reservations")
//    public List<ReservationResponseDTO> getUserReservations(@PathVariable Long userId) {
//        return adminService.getUserReservations(userId);
//    }
//
//    @GetMapping("/movies")
//    public List<MovieResponseDTO> getAllMovies() {
//        return adminService.getAllMovies();
//    }
//
//    @PostMapping("/movies")
//    public MovieResponseDTO createMovie(@RequestBody MovieDTO dto) {
//        return adminService.createMovie(dto);
//    }
//
//    @DeleteMapping("/movies/{id}")
//    public void deleteMovie(@PathVariable Long id) {
//        adminService.deleteMovie(id);
//    }
//
//    @GetMapping("/reservations")
//    public List<ReservationResponseDTO> getAllReservations() {
//        return adminService.getAllReservations();
//    }
//}
