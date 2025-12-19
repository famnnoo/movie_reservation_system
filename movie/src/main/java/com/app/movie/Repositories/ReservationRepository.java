package com.app.movie.Repositories;

import com.app.movie.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMovieId(Long movieId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByDisplayTimeId(Long displayTimeId);


}
