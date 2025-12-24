package bg.softuni.alpinevillas.service.impl;

import bg.softuni.alpinevillas.entities.*;
import bg.softuni.alpinevillas.repositories.BookingRepository;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.repositories.VillaRepository;
import bg.softuni.alpinevillas.service.BookingService;
import bg.softuni.alpinevillas.service.exception.BookingCreateException;
import bg.softuni.alpinevillas.service.exception.BookingMineActionException;
import bg.softuni.alpinevillas.service.exception.BookingOwnerActionException;
import bg.softuni.alpinevillas.web.dto.BookingCreateDto;
import bg.softuni.alpinevillas.web.dto.BookingViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    public BookingServiceImpl(BookingRepository bookingRepo,
                              VillaRepository villaRepo,
                              UserRepository userRepo) {
        this.bookingRepository = bookingRepo;
        this.villaRepository = villaRepo;
        this.userRepository = userRepo;
    }

    @Override
    @Transactional
    public UUID createBooking(UUID villaId, String username, BookingCreateDto dto) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new IllegalArgumentException("Вилата не е намерена."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен."));

        if (dto.getCheckIn() == null || dto.getCheckOut() == null) {
            throw new BookingCreateException(villaId, "Посочете валидни дати.");
        }
        if (!dto.getCheckIn().isBefore(dto.getCheckOut())) {
            throw new BookingCreateException(villaId, "Датите са невалидни!");
        }

        boolean overlaps = bookingRepository.existsByVilla_IdAndCheckOutAfterAndCheckInBefore(
                villaId, dto.getCheckIn(), dto.getCheckOut());
        if (overlaps) {
            throw new BookingCreateException(villaId, "Периодът е зает.");
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        BigDecimal total = villa.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setVilla(villa);
        booking.setUser(user);
        booking.setCheckIn(dto.getCheckIn());
        booking.setCheckOut(dto.getCheckOut());
        booking.setGuests(dto.getGuests());
        booking.setTotalPrice(total);
        booking.setStatus(BookingStatus.PENDING);

        log.info("User {} booked villa {} from {} to {}",
                booking.getUser().getUsername(),
                booking.getVilla().getName(),
                booking.getCheckIn(),
                booking.getCheckOut());

        if (villa.getOwner() != null && villa.getOwner().getUsername().equals(username)) {
            throw new BookingCreateException(villaId, "Не можете да резервирате собствената си вила.");
        }

        if (dto.getGuests() > villa.getCapacity()) {
            throw new BookingCreateException(villaId, "Гостите (" + dto.getGuests() + ") надвишават капацитета (" + villa.getCapacity() + ").");
        }

        bookingRepository.save(booking);
        return booking.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingViewDto> myBookings(String username) {
        return bookingRepository.findAllWithVillaByUserUsernameOrderByCheckInDesc(username)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingViewDto> ownerBookings(String ownerUsername) {
        return bookingRepository.findAllWithVillaAndUserByVillaOwnerUsernameOrderByCheckInDesc(ownerUsername)
                .stream()
                .map(this::toView)
                .toList();
    }

    private BookingViewDto toView(Booking booking) {
        BookingViewDto bv = new BookingViewDto();
                bv.setId(booking.getId());
                bv.setVillaName(booking.getVilla().getName());
                bv.setVillaRegion(booking.getVilla().getRegion());
                bv.setCheckIn(booking.getCheckIn());
                bv.setCheckOut(booking.getCheckOut());
                bv.setGuests(booking.getGuests());
                bv.setPricePerNight(booking.getVilla().getPricePerNight());

        java.math.BigDecimal fallback = booking.getVilla().getPricePerNight()
                .multiply(java.math.BigDecimal.valueOf(
                        java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut())
                ));

        bv.setTotalPrice(booking.getTotalPrice() != null ? booking.getTotalPrice() : fallback);
        bv.setStatus(booking.getStatus() != null ? booking.getStatus() : BookingStatus.PENDING);

                return bv;
    }

    @Override
    @Transactional
    public void confirm(UUID bookingId, String ownerUsername) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingOwnerActionException("Резервацията не е намерена."));

        if (!b.getVilla().getOwner().getUsername().equals(ownerUsername)) {
            throw new BookingOwnerActionException("Нямате права върху тази резервация.");
        }
        if (b.getStatus() != BookingStatus.PENDING) {
            throw new BookingOwnerActionException("Само PENDING резервации могат да бъдат потвърдени.");
        }

        b.setStatus(BookingStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public void decline(UUID bookingId, String ownerUsername) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingOwnerActionException("Резервацията не е намерена."));

        if (!b.getVilla().getOwner().getUsername().equals(ownerUsername)) {
            throw new BookingOwnerActionException("Нямате права върху тази резервация.");
        }
        if (b.getStatus() != BookingStatus.PENDING) {
            throw new BookingOwnerActionException("Само PENDING резервации могат да бъдат отказани.");
        }

        b.setStatus(BookingStatus.CANCELLED);
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId, String username) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingMineActionException("Резервацията не е намерена."));

        log.info("User {} cancelled booking {}",
                b.getUser().getUsername(),
                b.getId());

        if (!b.getUser().getUsername().equals(username)) {
            throw new BookingMineActionException("Това не е ваша резервация.");
        }
        if (b.getStatus() != BookingStatus.PENDING) {
            throw new BookingMineActionException("Само PENDING резервации могат да се отменят.");
        }
        if (!LocalDate.now().isBefore(b.getCheckIn())) {
            throw new BookingMineActionException("Не може да отмените в деня на настаняване или по-късно.");
        }

        b.setStatus(BookingStatus.CANCELLED);
    }
}
