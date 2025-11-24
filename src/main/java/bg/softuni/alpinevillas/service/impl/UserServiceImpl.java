package bg.softuni.alpinevillas.service.impl;

import bg.softuni.alpinevillas.entities.Role;
import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.repositories.RoleRepository;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.service.UserService;
import bg.softuni.alpinevillas.web.dto.RegistrationDto;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegistrationDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Паролите не съвпадат.");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Потребителското име е заето.");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Имейлът вече е регистриран.");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Липсва роля USER."));

        User u = new User();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setBlocked(false);
        u.getRoles().add(userRole);

        log.info("New user registered: {}", u.getUsername());

        userRepository.save(u);
    }

    @Override
    public void blockUser(String username, String adminName) {

        User userToBlock = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User admin = userRepository.findByUsername(adminName)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        userToBlock.setBlocked(true);

        userRepository.save(userToBlock);

        log.info("User {} blocked by {}", userToBlock.getUsername(), admin.getUsername());
    }
}

