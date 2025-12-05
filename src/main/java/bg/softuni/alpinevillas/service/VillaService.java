package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import bg.softuni.alpinevillas.web.dto.VillaDetailsDto;
import bg.softuni.alpinevillas.web.dto.VillaEditDto;

import java.util.List;
import java.util.UUID;

public interface VillaService {
    List<VillaDetailsDto> listAll();

    VillaDetailsDto getDetails(UUID id);

    UUID createVilla(VillaCreateDto dto, String ownerUsername);

    void deleteVilla(UUID id, String username);

    VillaEditDto getEditData(UUID id, String username);

    void editVilla(VillaEditDto dto, String username);

    void evictVillaCache(UUID villaId);


}
