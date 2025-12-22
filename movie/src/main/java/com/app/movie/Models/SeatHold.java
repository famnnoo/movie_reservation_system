package com.app.movie.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a temporary hold on seats (e.g., while user is in the booking process).
 * These holds expire after a certain time if not converted to actual reservations.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "seat_hold",
    indexes = {
        @Index(name = "idx_display_time_seat_hold", columnList = "display_time_id, seat_number"),
        @Index(name = "idx_expires_at", columnList = "expires_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_display_time_seat_hold",
            columnNames = {"display_time_id", "seat_number"}
        )
    }
)
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "display_time_id", nullable = false)
    private DisplayTime displayTime;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Default: hold expires after 5 minutes
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusMinutes(5);
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public long getSecondsRemaining() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }
}

