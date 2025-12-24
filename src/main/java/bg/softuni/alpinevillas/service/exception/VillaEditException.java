package bg.softuni.alpinevillas.service.exception;

import java.util.UUID;

public class VillaEditException extends RuntimeException {

    private final UUID villaId;

    public VillaEditException(String message, UUID villaId) {
        super(message);
        this.villaId = villaId;
    }

    public UUID getVillaId() {
        return villaId;
    }
}
