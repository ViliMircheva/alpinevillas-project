package bg.softuni.alpinevillas.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "amenities")
    private Set<Villa> villas = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Amenity setName(String name) {
        this.name = name;
        return this;
    }
    public Set<Villa> getVillas() {
        return villas;
    }

    public void setVillas(Set<Villa> villas) {
        this.villas = villas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amenity amenity)) return false;
        return id != null && id.equals(amenity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
