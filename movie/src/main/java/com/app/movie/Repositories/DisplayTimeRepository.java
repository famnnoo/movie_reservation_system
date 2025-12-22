package com.app.movie.Repositories;

import com.app.movie.Models.DisplayTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DisplayTimeRepository extends JpaRepository<DisplayTime, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT dt FROM DisplayTime dt WHERE dt.id = :id")
    Optional<DisplayTime> findByIdWithLock(@Param("id") Long id);
}
