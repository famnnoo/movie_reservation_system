package com.app.movie.Repositories;

import com.app.movie.Models.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByLocationCity(String city);
}
