package com.example.competr.portal.scheduler;

import com.example.competr.portal.service.RatingCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WeeklyRatingScheduler {

    @Autowired
    private RatingCalculationService ratingCalculationService;

    /**
     * Runs every Sunday at 2:00 AM to recalculate weekly ratings
     * Cron: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void recalculateWeeklyRatings() {
        try {
            System.out.println("Starting weekly rating recalculation at: " + LocalDateTime.now());
            ratingCalculationService.recalculateWeeklyRatings();
            System.out.println("Weekly rating recalculation completed at: " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error during weekly rating recalculation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manual trigger for testing - runs every 5 minutes (remove in production)
     */
    // @Scheduled(fixedRate = 300000) // 5 minutes
    public void testRatingRecalculation() {
        System.out.println("Test rating recalculation triggered at: " + LocalDateTime.now());
        ratingCalculationService.recalculateWeeklyRatings();
    }
}
