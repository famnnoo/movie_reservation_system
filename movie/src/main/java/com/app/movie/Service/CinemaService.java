package com.app.movie.Service;


import org.springframework.stereotype.Service;
import com.app.movie.Models.Cinema;
import com.app.movie.Models.Movie;
import com.app.movie.Repositories.CinemaRepository;
import com.app.movie.Repositories.MovieRepository;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;

    public CinemaService(CinemaRepository cinemaRepository, MovieRepository movieRepository) {
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
    }

    public Cinema addMovieToCinema(Long cinemaId, Long movieId) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        cinema.getMovies().add(movie);
        return cinemaRepository.save(cinema);
    }
}
