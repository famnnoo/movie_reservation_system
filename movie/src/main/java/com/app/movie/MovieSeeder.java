package com.app.movie;

import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Service.MovieService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MovieSeeder implements CommandLineRunner {

    private final MovieService movieService;

    public MovieSeeder(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 10; i++) {
            // Create movie DTO
            MovieDTO dto = new MovieDTO();
            dto.setTitle("Movie " + i);
            dto.setDescription("Description for movie " + i);
            dto.setDurationMinutes(100 + i);
            dto.setReleaseDate(LocalDate.of(2025, 12, i));
            dto.setTotalSeats(100);

            // Save movie
            MovieResponseDTO saved = movieService.createMovie(dto);

            // Load image from resources/static
            String filename = "cat" + i + ".png";
            InputStream is = new ClassPathResource("static/" + filename).getInputStream();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    filename, // use the original filename
                    "image/png",
                    is
            );

            // Upload image for the movie
            movieService.uploadMovieImage(saved.getId(), file);
            movieService.addDisplayTime(saved.getId(), LocalDateTime.of(2025, 12, i, 11, 30));
            movieService.addDisplayTime(saved.getId(), LocalDateTime.of(2025, 12, i, 15, 15));
            movieService.addDisplayTime(saved.getId(), LocalDateTime.of(2025, 12, i, 19, 45));

        }
    }
}
