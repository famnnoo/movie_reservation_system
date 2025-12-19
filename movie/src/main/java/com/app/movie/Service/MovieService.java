package com.app.movie.Service;

import com.app.movie.DTO.reservation.DisplayTimeResponseDTO;
import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Models.DisplayTime;
import com.app.movie.Models.Movie;
import com.app.movie.Repositories.DisplayTimeRepository;
import com.app.movie.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private final DisplayTimeRepository displayTimeRepository;

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

    public void addDisplayTime(Long movieId, LocalDateTime startTime) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        DisplayTime displayTime = new DisplayTime();
        displayTime.setMovie(movie);
        displayTime.setStartTime(startTime);

        displayTimeRepository.save(displayTime);
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

        // Prepend the folder path here
        Path imagePath = Paths.get("images/movies", movie.getImagePath());
        return Files.readAllBytes(imagePath);
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
                .genre(movie.getGenre())
                .cinemas(
                        movie.getCinemas() == null
                                ? Set.of()
                                : movie.getCinemas()
                                .stream()
                                .map(cinema -> cinema.getName())
                                .collect(Collectors.toSet())
                )
                .displayTimes(
                        movie.getDisplayTimes() == null
                                ? List.of()
                                : movie.getDisplayTimes()
                                .stream()
                                .map(dt -> DisplayTimeResponseDTO.builder()
                                        .id(dt.getId())
                                        .time(dt.getStartTime())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

}

