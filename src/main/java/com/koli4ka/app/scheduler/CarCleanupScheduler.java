package com.koli4ka.app.scheduler;

import com.koli4ka.app.car.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class CarCleanupScheduler {

    private final CarService carService;
    private static final int MONTHS_BEFORE_CLEANUP = 3;

    // Run at midnight every day
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldCars() {
        log.info("Starting scheduled cleanup of old car listings...");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(MONTHS_BEFORE_CLEANUP);
        List<UUID> oldCarIds = carService.findCarsPublishedBefore(cutoffDate);
        
        if (oldCarIds.isEmpty()) {
            log.info("No old car listings found for cleanup");
            return;
        }
        log.info("Cleanup completed. Deleted old car listings");
    }


} 