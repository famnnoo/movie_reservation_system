package com.app.movie.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.app.movie.Models.Cinema;
import com.app.movie.Repositories.CinemaRepository;
import java.util.List;

@RestController
@RequestMapping("/cinemas")
public class CinemaController {

    private final CinemaRepository cinemaRepository;

    public CinemaController(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

    @PostMapping
    public Cinema createCinema(@RequestBody Cinema cinema) {
        return cinemaRepository.save(cinema);
    }

    @GetMapping("/city/{city}")
    public List<Cinema> getCinemasByCity(@PathVariable String city) {
        return cinemaRepository.findByLocationCity(city);
    }
}
