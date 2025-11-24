package bg.softuni.alpinevillas.web.dto;

import bg.softuni.alpinevillas.entities.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BookingViewDto {

    private UUID id;
    private String villaName;
    private String villaRegion;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    private BookingStatus status;

    public String getVillaName() {
        return villaName;
    }

    public void setVillaName(String villaName) {
        this.villaName = villaName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVillaRegion() {
        return villaRegion;
    }

    public void setVillaRegion(String villaRegion) {
        this.villaRegion = villaRegion;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
