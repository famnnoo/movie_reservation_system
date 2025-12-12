package com.app.movie.Controllers;

import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<MovieResponseDTO> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponseDTO createMovie(@RequestBody MovieDTO dto) {
        return movieService.createMovie(dto);
    }
}
