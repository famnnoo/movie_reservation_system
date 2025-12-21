package com.app.movie.Controllers;

import com.app.movie.DTO.reservation.MovieDTO;
import com.app.movie.DTO.reservation.MovieResponseDTO;
import com.app.movie.Service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<MovieResponseDTO> getAllMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String cinema,
            @RequestParam(required = false) String date
    ) {
        // If any filter is provided, use filtering; otherwise return all
        if (search != null || genre != null || location != null || cinema != null || date != null) {
            return movieService.filterMovies(search, genre, location, cinema, date);
        }
        return movieService.getAllMovies();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponseDTO createMovie(@RequestBody MovieDTO dto) {
        return movieService.createMovie(dto);
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String filename = movieService.uploadMovieImage(id, file);
        return ResponseEntity.ok("Image uploaded: " + filename);
    }

    @GetMapping("/{id}/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        byte[] imageBytes = movieService.getMovieImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}

