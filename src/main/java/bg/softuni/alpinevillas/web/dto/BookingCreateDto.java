package bg.softuni.alpinevillas.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class BookingCreateDto {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @Min(1)
    private int guests;


    public @NotNull LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(@NotNull LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public @NotNull LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(@NotNull LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    @Min(1)
    public int getGuests() {
        return guests;
    }

    public void setGuests(@Min(1) int guests) {
        this.guests = guests;
    }
}
