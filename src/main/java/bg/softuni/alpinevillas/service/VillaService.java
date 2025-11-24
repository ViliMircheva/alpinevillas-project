package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import bg.softuni.alpinevillas.web.dto.VillaDetailsDto;

import java.util.List;
import java.util.UUID;

public interface VillaService {
    List<VillaDetailsDto> listAll();

    VillaDetailsDto getDetails(UUID id);

    UUID createVilla(VillaCreateDto dto, String ownerUsername);

    void deleteVilla(UUID id, String username);

}
