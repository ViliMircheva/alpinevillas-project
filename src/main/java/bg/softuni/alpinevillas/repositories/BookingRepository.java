package bg.softuni.alpinevillas.repositories;

import bg.softuni.alpinevillas.entities.Booking;
import bg.softuni.alpinevillas.entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByVilla_IdAndCheckOutAfterAndCheckInBefore(
            UUID villaId, LocalDate checkIn, LocalDate checkOut);

    @Query("""
    select b from Booking b
    join fetch b.villa v
    where b.user.username = :username
    order by b.checkIn desc
""")
    List<Booking> findAllWithVillaByUserUsernameOrderByCheckInDesc(String username);

    @Query("""
    select b from Booking b
    join fetch b.villa v
    join fetch b.user u
    where v.owner.username = :ownerUsername
    order by b.checkIn desc
""")
    List<Booking> findAllWithVillaAndUserByVillaOwnerUsernameOrderByCheckInDesc(String ownerUsername);


    List<Booking> findAllByStatusAndCheckOutLessThanEqual(
            BookingStatus status,
            LocalDate checkOut);
}
