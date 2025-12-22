package com.app.movie.Repositories;

import com.app.movie.Models.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    List<SeatHold> findByDisplayTimeId(Long displayTimeId);

    List<SeatHold> findByDisplayTimeIdAndUserId(Long displayTimeId, Long userId);

    Optional<SeatHold> findByDisplayTimeIdAndSeatNumber(Long displayTimeId, String seatNumber);

    @Query("SELECT sh FROM SeatHold sh WHERE sh.expiresAt < :now")
    List<SeatHold> findExpiredHolds(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SeatHold sh WHERE sh.expiresAt < :now")
    int deleteExpiredHolds(@Param("now") LocalDateTime now);

    void deleteByDisplayTimeIdAndUserId(Long displayTimeId, Long userId);
}

