package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.repositories.VillaRepository;
import bg.softuni.alpinevillas.service.impl.VillaServiceImpl;
import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VillaServiceImplTest {

    @Autowired
    private VillaServiceImpl villaService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VillaRepository villaRepository;

    @Test
    void testCreateVilla() {

        User owner = new User();
        owner.setUsername("testUser");
        owner.setEmail("t@t.bg");
        owner.setPassword("123");
        userRepository.save(owner);

        VillaCreateDto dto = new VillaCreateDto();
        dto.setName("TestVilla");
        dto.setRegion("Test");
        dto.setCapacity(3);
        dto.setPricePerNight(new BigDecimal("100.00"));

        UUID id = villaService.createVilla(dto, owner.getUsername());

        assertThat(villaRepository.findById(id)).isPresent();
    }
}
