package com.app.movie.Service;

import com.app.movie.DTO.UpdateUserDTO;
import com.app.movie.DTO.UserResponseDTO;
import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Models.Movie;
import com.app.movie.Models.Reservation;
import com.app.movie.Models.Role;
import com.app.movie.Models.User;
import com.app.movie.Repositories.MovieRepository;
import com.app.movie.Repositories.ReservationRepository;
import com.app.movie.Repositories.RoleRepository;
import com.app.movie.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReservationRepository reservationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------- USERS ----------------

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public List<ReservationResponseDTO> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToReservationResponseDTO)
                .collect(Collectors.toList());
    }

    // ---------------- MOVIES ----------------

    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToMovieResponseDTO)
                .collect(Collectors.toList());
    }

    public MovieResponseDTO createMovie(MovieDTO dto) {
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(dto.getReleaseDate())
                .totalSeats(dto.getTotalSeats())
                .build();

        Movie saved = movieRepository.save(movie);
        return convertToMovieResponseDTO(saved);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    // ---------------- RESERVATIONS ----------------

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToReservationResponseDTO)
                .collect(Collectors.toList());
    }

    // ---------------- HELPERS ----------------

    private ReservationResponseDTO convertToReservationResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .movieId(reservation.getMovie().getId())
                .seatNumbers(reservation.getSeatNumbers())
                .reservationDate(reservation.getReservationDate())
                .build();
    }

    private MovieResponseDTO convertToMovieResponseDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .totalSeats(movie.getTotalSeats())
                .build();
    }
}
