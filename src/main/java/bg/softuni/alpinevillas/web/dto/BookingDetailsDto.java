package bg.softuni.alpinevillas.web.dto;

import bg.softuni.alpinevillas.entities.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BookingDetailsDto {

    private UUID id;
    private UUID villaId;
    private String villaName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal totalPrice;
    private BookingStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVillaId() {
        return villaId;
    }

    public void setVillaId(UUID villaId) {
        this.villaId = villaId;
    }

    public String getVillaName() {
        return villaName;
    }

    public void setVillaName(String villaName) {
        this.villaName = villaName;
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
