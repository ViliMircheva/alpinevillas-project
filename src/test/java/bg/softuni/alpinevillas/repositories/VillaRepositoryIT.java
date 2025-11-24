package bg.softuni.alpinevillas.repositories;

import bg.softuni.alpinevillas.entities.Villa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VillaRepositoryIT {

    @Autowired
    private VillaRepository villaRepository;

    @Test
    void testSaveAndFindById() {
        Villa villa = new Villa();
        villa.setName("Test Villa");
        villa.setRegion("Test");
        villa.setCapacity(5);
        villa.setPricePerNight(new BigDecimal("123.45"));

        Villa saved = villaRepository.save(villa);

        Optional<Villa> found = villaRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Villa");
    }
}
