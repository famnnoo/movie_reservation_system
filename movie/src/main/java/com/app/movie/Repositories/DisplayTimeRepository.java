package com.app.movie.Repositories;

import com.app.movie.Models.DisplayTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisplayTimeRepository extends JpaRepository<DisplayTime, Long> {
    List<DisplayTime> findByMovieId(Long movieId);
}
