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
    private final RoleRepository roleRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DatabaseSeeder(LocationRepository locationRepository,
                          CinemaRepository cinemaRepository,
                          MovieRepository movieRepository,
                          DisplayTimeRepository displayTimeRepository,
                          ReservationRepository reservationRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.locationRepository = locationRepository;
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
        this.displayTimeRepository = displayTimeRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Standard seat layout: 10 rows (A-J) x 15 seats per row = 150 total seats
        final int TOTAL_SEATS = 150;

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

            movie.setDurationMinutes(90 + (i * 10) + random.nextInt(30)); // 90-210 minutes range
            movie.setTotalSeats(TOTAL_SEATS); // All movies now have standardized 150 seats
            movie.setReleaseDate(LocalDate.now().minusDays((i + 1) * 10));
            movie.setCreatedAt(LocalDateTime.now().minusDays(i + 1));
            
            // Generate realistic IMDb rating between 5.0 and 9.5
            double imdbRating = 5.0 + (random.nextDouble() * 4.5);
            movie.setImdbRating(Math.round(imdbRating * 10.0) / 10.0); // Round to 1 decimal

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

        // Get roles
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        // Create admin user
        User admin = User.builder()
                .name("Admin User")
                .email("admin@example.com")
                .passwordHash(passwordEncoder.encode("admin123")) // Properly hashed password
                .roles(new HashSet<>(Arrays.asList(userRole, adminRole)))
                .build();
        userRepository.save(admin);
        System.out.println("✅ Created admin user: admin@example.com / admin123");

        // Create regular users
        for (int i = 1; i <= 50; i++) {
            User user = User.builder()
                    .name("User " + i)
                    .email("user" + i + "@example.com")
                    .passwordHash(passwordEncoder.encode("password")) // Properly hashed
                    .roles(new HashSet<>(Collections.singletonList(userRole)))
                    .build();

            // Random createdAt / updatedAt in past 60 days
            int daysOffset = random.nextInt(60);
            LocalDateTime created = LocalDateTime.now().minusDays(daysOffset);
            user.setCreatedAt(created);
            user.setUpdatedAt(created.plusHours(random.nextInt(48)));

            userRepository.save(user);
        }
    }

    /**
     * Generate all seat numbers matching the standardized layout:
     * 10 rows (A-J) x 15 seats per row = 150 total seats
     * This matches the seat structure in SeatService.
     */
    private List<String> generateAllSeats() {
        List<String> seats = new ArrayList<>();
        for (char row = 'A'; row <= 'J'; row++) {
            for (int num = 1; num <= 15; num++) {
                seats.add(row + String.valueOf(num));
            }
        }
        return seats;
    }

    /**
     * Seed reservations for display times.
     * Creates 1-3 reservations per display time, with each reservation having 1-4 seats.
     * Properly tracks taken seats to avoid double-booking.
     * Works with the new seat structure: A1-J15 (150 seats total).
     */
    private void seedReservations() {

        if (reservationRepository.count() > 0) return;

        List<User> users = userRepository.findAll();
        List<DisplayTime> displayTimes = displayTimeRepository.findAll();
        Random random = new Random();

        for (DisplayTime displayTime : displayTimes) {

            // Track seats already taken for this specific display time
            Set<String> takenSeats = new HashSet<>();
            List<String> allSeats = generateAllSeats(); // Generate all 150 seats (A1-J15)
            Collections.shuffle(allSeats); // Randomize seat selection

            // Create 1-3 reservations per display time
            int reservationsForDisplay = 1 + random.nextInt(3);

            for (int r = 0; r < reservationsForDisplay; r++) {

                Reservation reservation = new Reservation();
                User user = users.get(random.nextInt(users.size()));

                reservation.setUser(user);
                reservation.setMovie(displayTime.getMovie());
                reservation.setDisplayTime(displayTime);
                reservation.setReservationDate(
                        LocalDateTime.now().minusDays(random.nextInt(30))
                );

                reservation.setSeatNumbers(new HashSet<>());

                // Each reservation gets 1-4 seats
                int seatsCount = 1 + random.nextInt(4);

                for (int s = 0; s < seatsCount && !allSeats.isEmpty(); s++) {
                    String seat = allSeats.remove(0); // Get next available seat
                    takenSeats.add(seat);

                    ReservationSeatNumber seatNumber = new ReservationSeatNumber();
                    seatNumber.setSeatNumbers(seat);
                    seatNumber.setReservation(reservation);
                    reservation.getSeatNumbers().add(seatNumber);
                }

                // Only save if at least one seat was reserved
                if (!reservation.getSeatNumbers().isEmpty()) {
                    reservationRepository.save(reservation);
                }
            }
        }
    }

}
