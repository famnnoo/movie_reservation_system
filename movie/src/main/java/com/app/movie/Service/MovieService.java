package com.app.movie.Service;

import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Models.Movie;
import com.app.movie.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final String uploadDir = "images/movies/";

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

    public String uploadMovieImage(Long movieId, MultipartFile file) throws IOException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + filename);

        Files.createDirectories(filePath.getParent());

        Files.copy(file.getInputStream(), filePath);

        movie.setImagePath(filename);
        movieRepository.save(movie);

        return filename;
    }

    public byte[] getMovieImage(Long movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (movie.getImagePath() == null) throw new RuntimeException("No image for this movie");

        Path filePath = Paths.get(uploadDir + movie.getImagePath());
        return Files.readAllBytes(filePath);
    }

    private MovieResponseDTO convertToResponseDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .totalSeats(movie.getTotalSeats())
                .imagePath(movie.getImagePath())
                .build();
    }
}

