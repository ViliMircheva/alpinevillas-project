package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.entities.Booking;
import bg.softuni.alpinevillas.entities.BookingStatus;
import bg.softuni.alpinevillas.repositories.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {
    public static final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private final BookingRepository bookingRepository;

    private final CacheManager cacheManager;

    public MaintenanceService(BookingRepository bookingRepository, CacheManager cacheManager) {
        this.bookingRepository = bookingRepository;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 14 * * *")
    @Transactional
    public void finishExpiredBookings() {
        LocalDate today = LocalDate.now();

        List<Booking> finishedBookings =
                bookingRepository.findAllByStatusAndCheckOutLessThanEqual(
                        BookingStatus.CONFIRMED, today);

        finishedBookings.forEach(b -> {
            b.setStatus(BookingStatus.FINISHED);
            log.info("Auto-finished booking {} for villa {}",
                    b.getId(), b.getVilla().getName());
        });

        if (!finishedBookings.isEmpty()) {
            log.info("Total auto-finished bookings: {}", finishedBookings.size());
        }
    }


    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void clearVillaCaches() {
        Cache catalogCache = cacheManager.getCache("villasCatalog");
        Cache detailsCache = cacheManager.getCache("villaDetails");

        if (catalogCache != null) catalogCache.clear();
        if (detailsCache != null) detailsCache.clear();

        log.info("Cleared villa caches (villasCatalog, villaDetails).");
    }

}
