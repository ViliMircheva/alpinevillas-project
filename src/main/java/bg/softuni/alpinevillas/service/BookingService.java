package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.web.dto.BookingCreateDto;
import bg.softuni.alpinevillas.web.dto.BookingViewDto;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    UUID createBooking(UUID villaId, String username, BookingCreateDto dto);

    List<BookingViewDto> myBookings(String username);

    List<BookingViewDto> ownerBookings(String ownerUsername);

    void confirm(UUID bookingId, String ownerUsername);
    void decline(UUID bookingId, String ownerUsername);
    void cancel(UUID bookingId, String username);

}
