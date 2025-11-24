package bg.softuni.alpinevillas.repositories;

import bg.softuni.alpinevillas.entities.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AmenityRepository extends JpaRepository <Amenity, UUID> {
    Optional<Amenity> findByName(String name);
}
