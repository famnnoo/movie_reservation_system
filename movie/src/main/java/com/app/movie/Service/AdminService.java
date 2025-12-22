package com.app.movie.Service;

import com.app.movie.DTO.UpdateUserDTO;
import com.app.movie.DTO.UserResponseDTO;
import com.app.movie.DTO.auth.UserDTO;
import com.app.movie.DTO.reservation.ReservationResponseDTO;
import com.app.movie.Models.*;
import com.app.movie.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ==================== User Management ====================

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .roles(new HashSet<>())
                .build();

        // Add default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.getRoles().add(userRole);

        User saved = userRepository.save(user);
        return convertToUserResponseDTO(saved);
    }

    public UserResponseDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        User updated = userRepository.save(user);
        return convertToUserResponseDTO(updated);
    }

    public void deleteUser(Long id) {
        // Get current authenticated user
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Prevent user from deleting their own account
        if (currentUser.getId().equals(id)) {
            throw new RuntimeException("You cannot delete your own account");
        }

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO toggleAdminRole(Long userId) {
        // Get current authenticated user
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Prevent user from removing their own admin role
        if (currentUser.getId().equals(userId)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getRoles().contains(adminRole)) {
                throw new RuntimeException("You cannot remove your own admin privileges");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        if (user.getRoles().contains(adminRole)) {
            user.getRoles().remove(adminRole);
        } else {
            user.getRoles().add(adminRole);
        }

        User updated = userRepository.save(user);
        return convertToUserResponseDTO(updated);
    }

    // ==================== Reservation Management ====================

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToReservationResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToReservationResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteReservation(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new RuntimeException("Reservation not found");
        }
        reservationRepository.deleteById(reservationId);
    }

    // ==================== Helper Methods ====================

    private UserResponseDTO convertToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private ReservationResponseDTO convertToReservationResponseDTO(Reservation reservation) {
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
