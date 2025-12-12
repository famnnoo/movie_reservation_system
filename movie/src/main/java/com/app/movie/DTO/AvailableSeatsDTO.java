package com.app.movie.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailableSeatsDTO {
    private Long movieId;
    private List<String> availableSeats;
}