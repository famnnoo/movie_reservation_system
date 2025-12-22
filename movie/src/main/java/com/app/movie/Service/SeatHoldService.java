package com.app.movie.Service;

import com.app.movie.DTO.SeatStatusDTO;
import com.app.movie.Models.DisplayTime;
import com.app.movie.Models.SeatHold;
import com.app.movie.Models.User;
import com.app.movie.Repositories.DisplayTimeRepository;
import com.app.movie.Repositories.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;
    private final DisplayTimeRepository displayTimeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int HOLD_DURATION_MINUTES = 5;

    /**
     * Hold seats for a user (temporarily during booking process)
     */
    @Transactional
    public List<SeatHold> holdSeats(Long displayTimeId, Set<String> seatNumbers, User user) {
        DisplayTime displayTime = displayTimeRepository.findById(displayTimeId)
                .orElseThrow(() -> new RuntimeException("DisplayTime not found"));

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);

        List<SeatHold> holds = seatNumbers.stream().map(seatNumber -> {
            // Check if seat is already held or reserved
            seatHoldRepository.findByDisplayTimeIdAndSeatNumber(displayTimeId, seatNumber)
                    .ifPresent(existing -> {
                        if (!existing.isExpired() && !existing.getUser().getId().equals(user.getId())) {
                            throw new RuntimeException("Seat " + seatNumber + " is currently held by another user");
                        }
                        // Remove expired or own holds
                        seatHoldRepository.delete(existing);
                    });

            SeatHold hold = SeatHold.builder()
                    .displayTime(displayTime)
                    .seatNumber(seatNumber)
                    .user(user)
                    .expiresAt(expiresAt)
                    .build();

            return seatHoldRepository.save(hold);
        }).collect(Collectors.toList());

        // Broadcast seat status update via WebSocket
        broadcastSeatUpdates(displayTimeId, seatNumbers, SeatStatusDTO.SeatStatus.HELD, user.getId());

        return holds;
    }

    /**
     * Release held seats (when user cancels or navigates away)
     */
    @Transactional
    public void releaseSeats(Long displayTimeId, Set<String> seatNumbers, User user) {
        seatNumbers.forEach(seatNumber -> {
            seatHoldRepository.findByDisplayTimeIdAndSeatNumber(displayTimeId, seatNumber)
                    .ifPresent(hold -> {
                        if (hold.getUser().getId().equals(user.getId())) {
                            seatHoldRepository.delete(hold);
                            // Broadcast that seat is now available
                            broadcastSeatUpdate(displayTimeId, seatNumber, SeatStatusDTO.SeatStatus.AVAILABLE, null);
                        }
                    });
        });
    }

    /**
     * Release all holds for a user on a specific display time
     */
    @Transactional
    public void releaseAllUserSeats(Long displayTimeId, Long userId) {
        List<SeatHold> userHolds = seatHoldRepository.findByDisplayTimeIdAndUserId(displayTimeId, userId);
        
        userHolds.forEach(hold -> {
            broadcastSeatUpdate(displayTimeId, hold.getSeatNumber(), 
                SeatStatusDTO.SeatStatus.AVAILABLE, null);
        });

        seatHoldRepository.deleteByDisplayTimeIdAndUserId(displayTimeId, userId);
    }

    /**
     * Get all currently held seats for a display time
     */
    public List<SeatHold> getHeldSeats(Long displayTimeId) {
        return seatHoldRepository.findByDisplayTimeId(displayTimeId).stream()
                .filter(hold -> !hold.isExpired())
                .collect(Collectors.toList());
    }

    /**
     * Clean up expired holds (called by scheduled task)
     */
    @Transactional
    public void cleanupExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatHold> expiredHolds = seatHoldRepository.findExpiredHolds(now);

        expiredHolds.forEach(hold -> {
            log.info("Releasing expired hold for seat {} on display time {}", 
                hold.getSeatNumber(), hold.getDisplayTime().getId());
            
            // Broadcast that seat is now available
            broadcastSeatUpdate(
                hold.getDisplayTime().getId(),
                hold.getSeatNumber(),
                SeatStatusDTO.SeatStatus.AVAILABLE,
                null
            );
        });

        int deleted = seatHoldRepository.deleteExpiredHolds(now);
        if (deleted > 0) {
            log.info("Cleaned up {} expired seat holds", deleted);
        }
    }

    /**
     * Broadcast seat status update to all connected clients
     */
    private void broadcastSeatUpdate(Long displayTimeId, String seatNumber, 
                                    SeatStatusDTO.SeatStatus status, Long userId) {
        SeatStatusDTO update = SeatStatusDTO.builder()
                .displayTimeId(displayTimeId)
                .seatNumber(seatNumber)
                .status(status)
                .userId(userId)
                .build();

        messagingTemplate.convertAndSend("/topic/seats/" + displayTimeId, update);
    }

    /**
     * Broadcast multiple seat updates
     */
    private void broadcastSeatUpdates(Long displayTimeId, Set<String> seatNumbers, 
                                     SeatStatusDTO.SeatStatus status, Long userId) {
        seatNumbers.forEach(seatNumber -> 
            broadcastSeatUpdate(displayTimeId, seatNumber, status, userId));
    }
}

