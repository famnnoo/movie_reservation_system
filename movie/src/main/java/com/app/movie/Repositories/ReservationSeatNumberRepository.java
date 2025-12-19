package com.app.movie.Repositories;

import com.app.movie.Models.ReservationSeatNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSeatNumberRepository extends JpaRepository<ReservationSeatNumber, Long> {
}
