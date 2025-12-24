package bg.softuni.alpinevillas.service.exception;

public class RoleMissingException extends RuntimeException {
    public RoleMissingException(String message) {
        super(message);
    }
}
