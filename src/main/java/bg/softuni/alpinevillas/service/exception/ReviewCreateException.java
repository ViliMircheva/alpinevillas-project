package bg.softuni.alpinevillas.service.exception;

import java.util.UUID;

public class ReviewCreateException extends RuntimeException {

    private final UUID villaId;

    public ReviewCreateException(UUID villaId, String message) {
        super(message);
        this.villaId = villaId;
    }

    public UUID getVillaId() {
        return villaId;
    }
}
