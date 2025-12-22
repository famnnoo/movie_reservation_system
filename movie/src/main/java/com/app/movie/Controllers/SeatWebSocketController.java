package com.app.movie.Controllers;

import com.app.movie.DTO.SeatStatusDTO;
import com.app.movie.Models.SeatHold;
import com.app.movie.Models.User;
import com.app.movie.Service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SeatWebSocketController {

    private final SeatHoldService seatHoldService;

    /**
     * Client requests to hold seats
     * Message format: { "displayTimeId": 1, "seatNumbers": ["A1", "A2"] }
     */
    @MessageMapping("/seats/hold")
    @SendTo("/topic/seats/{displayTimeId}")
    public SeatStatusDTO holdSeats(HoldSeatsRequest request, @AuthenticationPrincipal User user) {
        log.info("User {} requesting to hold seats: {}", user.getId(), request.getSeatNumbers());
        
        try {
            seatHoldService.holdSeats(request.getDisplayTimeId(), request.getSeatNumbers(), user);
            
            return SeatStatusDTO.builder()
                    .displayTimeId(request.getDisplayTimeId())
                    .status(SeatStatusDTO.SeatStatus.HELD)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            log.error("Error holding seats", e);
            throw e;
        }
    }

    /**
     * Client requests to release seats
     */
    @MessageMapping("/seats/release")
    @SendTo("/topic/seats/{displayTimeId}")
    public SeatStatusDTO releaseSeats(ReleaseSeatsRequest request, @AuthenticationPrincipal User user) {
        log.info("User {} releasing seats: {}", user.getId(), request.getSeatNumbers());
        
        seatHoldService.releaseSeats(request.getDisplayTimeId(), request.getSeatNumbers(), user);
        
        return SeatStatusDTO.builder()
                .displayTimeId(request.getDisplayTimeId())
                .status(SeatStatusDTO.SeatStatus.AVAILABLE)
                .build();
    }

    /**
     * DTOs for WebSocket messages
     */
    public static class HoldSeatsRequest {
        private Long displayTimeId;
        private Set<String> seatNumbers;

        public Long getDisplayTimeId() { return displayTimeId; }
        public void setDisplayTimeId(Long displayTimeId) { this.displayTimeId = displayTimeId; }
        public Set<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(Set<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }

    public static class ReleaseSeatsRequest {
        private Long displayTimeId;
        private Set<String> seatNumbers;

        public Long getDisplayTimeId() { return displayTimeId; }
        public void setDisplayTimeId(Long displayTimeId) { this.displayTimeId = displayTimeId; }
        public Set<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(Set<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }
}

