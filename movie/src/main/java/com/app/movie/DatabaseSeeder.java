package com.app.movie;

import com.app.movie.Models.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.app.movie.Repositories.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;
    private final DisplayTimeRepository displayTimeRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public DatabaseSeeder(LocationRepository locationRepository,
                          CinemaRepository cinemaRepository,
                          MovieRepository movieRepository,
                          DisplayTimeRepository displayTimeRepository,
                          ReservationRepository reservationRepository,
                          UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
        this.displayTimeRepository = displayTimeRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedLocations();
        seedCinemas();
        seedMovies();
        seedCinemaMovies();
        seedDisplayTimes();
        seedUsers();
        seedReservations();
    }

    private void seedLocations() {
        if (locationRepository.count() > 0) return;

        for (int i = 1; i <= 50; i++) {
            Location location = new Location();
            location.setCity("City" + i);
            location.setAddress(i + " Main Street");
            location.setLatitude(40.0 + i * 0.1);
            location.setLongitude(-74.0 + i * 0.1);
            locationRepository.save(location);
        }
    }

    private void seedCinemas() {
        if (cinemaRepository.count() > 0) return;

        List<Location> locations = locationRepository.findAll();
        Random random = new Random();
        for (int i = 1; i <= 50; i++) {
            Cinema cinema = new Cinema();
            cinema.setName("Cinema " + i);
            cinema.setLocation(locations.get(random.nextInt(locations.size())));
            cinemaRepository.save(cinema);
        }
    }

    private void seedMovies() throws IOException {
        Path imagesFolder = Paths.get("images/movies");

        // Get first 10 files
        List<Path> movieFiles = Files.list(imagesFolder)
                .limit(10)
                .toList();

        if (movieRepository.count() > 0) return;

        String[] genrePool = {
                "Action", "Drama", "Comedy", "Thriller", "Romance", "Horror", "Sci-Fi", "Fantasy", "Adventure", "Mystery"
        };
        Random random = new Random();

        for (int i = 0; i < movieFiles.size(); i++) {
            Path movieFile = movieFiles.get(i);

            Movie movie = new Movie();
            movie.setTitle("Movie " + (i + 1));
            movie.setDescription("Description for movie " + (i + 1));

            // Pick 2-3 random genres per movie
            int genreCount = 2 + random.nextInt(2); // 2 or 3 genres
            Set<String> genres = new HashSet<>();
            while (genres.size() < genreCount) {
                genres.add(genrePool[random.nextInt(genrePool.length)]);
            }
            movie.setGenre(String.join(",", genres));

            movie.setDurationMinutes(90 + i);
            movie.setTotalSeats(50 + i);
            movie.setReleaseDate(LocalDate.now().minusDays((i + 1) * 10));
            movie.setCreatedAt(LocalDateTime.now().minusDays(i + 1));

            // Store only the filename
            movie.setImagePath(movieFile.getFileName().toString());

            movieRepository.save(movie);
        }
    }


    private void seedCinemaMovies() {
        List<Cinema> cinemas = cinemaRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        Random random = new Random();

        for (Cinema cinema : cinemas) {
            Set<Movie> cinemaMovies = new HashSet<>();
            int movieCount = 5 + random.nextInt(6); // 5-10 movies per cinema
            while (cinemaMovies.size() < movieCount) {
                cinemaMovies.add(movies.get(random.nextInt(movies.size())));
            }
            cinema.setMovies(cinemaMovies);
            cinemaRepository.save(cinema);
        }
    }

    private void seedDisplayTimes() {
        List<Movie> movies = movieRepository.findAll();
        Random random = new Random();

        for (Movie movie : movies) {
            for (int i = 0; i < 50; i++) { // 50 display times per movie
                DisplayTime displayTime = new DisplayTime();
                displayTime.setMovie(movie);

                // Random past or future date
                int daysOffset = random.nextInt(60) - 30; // -30 to +29 days
                int hourOffset = random.nextInt(24);      // random hour
                displayTime.setStartTime(LocalDateTime.now().plusDays(daysOffset).withHour(hourOffset).withMinute(0));

                displayTimeRepository.save(displayTime);
            }
        }
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        Random random = new Random();

        for (int i = 1; i <= 50; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("user" + i + "@example.com");
            user.setPasswordHash("password"); // hash in real app

            // Random createdAt / updatedAt in past 60 days
            int daysOffset = random.nextInt(60);
            LocalDateTime created = LocalDateTime.now().minusDays(daysOffset);
            user.setCreatedAt(created);
            user.setUpdatedAt(created.plusHours(random.nextInt(48)));

            userRepository.save(user);
        }
    }

    private void seedReservations() {
        List<User> users = userRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        Random random = new Random();
        List<DisplayTime> displayTimes = displayTimeRepository.findAll();

        for (int i = 0; i < 50; i++) {
            Reservation reservation = new Reservation();

            User user = users.get(random.nextInt(users.size()));
            Movie movie = movies.get(random.nextInt(movies.size()));

            // Pick a random DisplayTime for this movie
            List<DisplayTime> movieDisplayTimes = displayTimes.stream()
                    .filter(dt -> dt.getMovie().getId().equals(movie.getId()))
                    .toList();
            DisplayTime displayTime = movieDisplayTimes.get(random.nextInt(movieDisplayTimes.size()));

            reservation.setUser(user);
            reservation.setMovie(movie);
            reservation.setDisplayTime(displayTime);

            int daysOffset = random.nextInt(60) - 30;
            int hoursOffset = random.nextInt(24);
            reservation.setReservationDate(LocalDateTime.now().plusDays(daysOffset).plusHours(hoursOffset));

            reservation.setSeatNumbers(new HashSet<>());
            int seatsCount = 1 + random.nextInt(5);
            for (int s = 0; s < seatsCount; s++) {
                char row = (char) ('A' + random.nextInt(10));
                int seatNum = 1 + random.nextInt(15);
                String seatLabel = String.valueOf(row + seatNum);

                ReservationSeatNumber seatNumber = new ReservationSeatNumber();
                seatNumber.setSeatNumbers(seatLabel);
                seatNumber.setReservation(reservation);
                reservation.getSeatNumbers().add(seatNumber);
            }

            reservationRepository.save(reservation);
        }
    }
}
