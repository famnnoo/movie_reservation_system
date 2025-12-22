package com.app.movie.Scheduling;

import com.app.movie.Service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task to clean up expired seat holds.
 * Runs every 30 seconds to release seats that have passed their expiration time.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeatHoldCleanupTask {

    private final SeatHoldService seatHoldService;

    /**
     * Clean up expired holds every 30 seconds
     */
    @Scheduled(fixedDelay = 30000) // 30 seconds
    public void cleanupExpiredHolds() {
        try {
            seatHoldService.cleanupExpiredHolds();
        } catch (Exception e) {
            log.error("Error cleaning up expired seat holds", e);
        }
    }
}

