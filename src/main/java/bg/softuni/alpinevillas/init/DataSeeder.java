package bg.softuni.alpinevillas.init;

import bg.softuni.alpinevillas.entities.Amenity;
import bg.softuni.alpinevillas.entities.Role;
import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.repositories.AmenityRepository;
import bg.softuni.alpinevillas.repositories.RoleRepository;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.repositories.VillaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final AmenityRepository amenityRepo;
    private final VillaRepository villaRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(RoleRepository roleRepo,
                      UserRepository userRepo,
                      AmenityRepository amenityRepo,
                      VillaRepository villaRepo,
                      PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.amenityRepo = amenityRepo;
        this.villaRepo = villaRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Role userRole = roleRepo.findByName("USER").orElseGet(() -> {
            Role r = new Role().setName("USER");
            return roleRepo.save(r);
        });

        Role adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> {
            Role r = new Role().setName("ADMIN");
            return roleRepo.save(r);
        });

        User admin = userRepo.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setEmail("admin@alpine.villas");
            u.setPassword(encoder.encode("admin123"));
            u.setBlocked(false);
            u.getRoles().addAll(Set.of(userRole, adminRole));
            return userRepo.save(u);
        });

        User owner = userRepo.findByUsername("owner").orElseGet(() -> {
            User u = new User();
            u.setUsername("owner");
            u.setEmail("owner@alpine.villas");
            u.setPassword(encoder.encode("owner123"));
            u.setBlocked(false);
            u.getRoles().add(userRole);
            return userRepo.save(u);
        });

        List<String> amenityNames = List.of("Fireplace", "Sauna", "Hot tub", "Wi-Fi", "Parking", "Breakfast", "Pool");
        amenityNames.forEach(n ->
                amenityRepo.findByName(n)
                        .orElseGet(() -> amenityRepo.save(new Amenity().setName(n)))
        );

        log.info("Seed complete: roles={}, users={}, amenities={}, villas={}",
                roleRepo.count(), userRepo.count(), amenityRepo.count(), villaRepo.count());
    }

}
