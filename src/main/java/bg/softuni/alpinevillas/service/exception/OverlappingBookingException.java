package bg.softuni.alpinevillas.service.exception;

public class OverlappingBookingException extends RuntimeException {
    public OverlappingBookingException(String message) {
        super(message);
    }
}
