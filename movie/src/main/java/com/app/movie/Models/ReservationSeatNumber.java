package com.app.movie.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "reservation_seat_number",
    indexes = {
        @Index(name = "idx_display_time_seat", columnList = "display_time_id, seat_numbers")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_display_time_seat", 
            columnNames = {"display_time_id", "seat_numbers"}
        )
    }
)
public class ReservationSeatNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_numbers", nullable = false)
    private String seatNumbers; // e.g., "A1"

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "display_time_id", nullable = false)
    private DisplayTime displayTime;
}
