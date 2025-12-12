package com.app.movie.Service;

import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Models.Movie;
import com.app.movie.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public MovieResponseDTO createMovie(MovieDTO dto) {
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(dto.getReleaseDate())
                .totalSeats(dto.getTotalSeats())
                .build();
        Movie saved = movieRepository.save(movie);
        return convertToResponseDTO(saved);
    }
    private MovieResponseDTO convertToResponseDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .totalSeats(movie.getTotalSeats())
                .build();
    }
}
