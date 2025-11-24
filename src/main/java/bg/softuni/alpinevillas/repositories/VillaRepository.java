package bg.softuni.alpinevillas.repositories;

import bg.softuni.alpinevillas.entities.Villa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface VillaRepository extends JpaRepository <Villa, UUID> {

    @Query("""
       select v
       from Villa v
       left join fetch v.owner
       left join fetch v.amenities
       where v.id = :id
       """)
    Optional<Villa> findByIdWithAmenities(UUID id);
}

