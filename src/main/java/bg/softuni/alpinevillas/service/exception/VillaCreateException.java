package bg.softuni.alpinevillas.service.exception;

import bg.softuni.alpinevillas.web.dto.VillaCreateDto;

public class VillaCreateException extends RuntimeException {

    private final VillaCreateDto dto;

    public VillaCreateException(String message, VillaCreateDto dto) {
        super(message);
        this.dto = dto;
    }

    public VillaCreateDto getDto() {
        return dto;
    }
}
