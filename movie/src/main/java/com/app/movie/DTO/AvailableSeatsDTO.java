package com.app.movie.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailableSeatsDTO {
    private Long movieId;
    private List<SeatDTO> seats;
    private int rows;           // e.g., 10 (A-J)
    private int seatsPerRow;    // e.g., 15
}