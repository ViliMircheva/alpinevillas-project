package bg.softuni.alpinevillas.service.impl;

import bg.softuni.alpinevillas.entities.Role;
import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.repositories.RoleRepository;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.service.UserService;
import bg.softuni.alpinevillas.service.exception.*;
import bg.softuni.alpinevillas.web.dto.RegistrationDto;
import bg.softuni.alpinevillas.web.dto.UserAdminViewDto;
import bg.softuni.alpinevillas.web.dto.UserProfileDto;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegistrationDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RegistrationException("Паролите не съвпадат.");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RegistrationException("Потребителското име е заето.");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegistrationException("Имейлът вече е регистриран.");
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
    @Transactional
    public List<UserAdminViewDto> getAllUsersForAdmin() {
        return userRepository.findAll()
                .stream()
                .map(u -> {
                    UserAdminViewDto dto = new UserAdminViewDto();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setEmail(u.getEmail());
                    dto.setBlocked(u.isBlocked());
                    dto.setRoles(
                            u.getRoles().stream()
                                    .map(Role::getName)
                                    .toList()
                    );
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void blockUser(UUID userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new AdminUserActionException("Админът не е намерен."));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));

        if (!isAdmin) {
            throw new AdminUserActionException("Нямате права да блокирате потребители.");
        }

        if (admin.getId().equals(userId)) {
            throw new AdminUserActionException("Не можете да блокирате себе си.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdminUserActionException("Потребителят не е намерен."));

        if (user.isBlocked()) {
            return;
        }

        user.setBlocked(true);
        userRepository.save(user);

        log.info("User {} blocked by {}", user.getUsername(), admin.getUsername());
    }

    @Override
    @Transactional
    public void unblockUser(UUID userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new AdminUserActionException("Админът не е намерен."));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));
        if (!isAdmin) {
            throw new AdminUserActionException("Нямате права да деблокирате потребители.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdminUserActionException("Потребителят не е намерен."));

        if (!user.isBlocked()) {
            return;
        }

        user.setBlocked(false);
        userRepository.save(user);

        log.info("User {} unblocked by {}", user.getUsername(), admin.getUsername());
    }


    @Override
    @Transactional
    public void grantAdmin(UUID userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new AdminUserActionException("Админът не е намерен."));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));
        if (!isAdmin) {
            throw new AdminUserActionException("Нямате права да променяте роли.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdminUserActionException("Потребителят не е намерен."));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new AdminUserActionException("Роля ADMIN липсва."));

        boolean alreadyAdmin = user.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));

        if (alreadyAdmin) {
            return;
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);

        log.info("User {} granted ADMIN by {}", user.getUsername(), admin.getUsername());
    }


    @Override
    @Transactional
    public void revokeAdmin(UUID userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new AdminUserActionException("Админът не е намерен."));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));
        if (!isAdmin) {
            throw new AdminUserActionException("Нямате права да променяте роли.");
        }
        if (admin.getId().equals(userId)) {
            throw new AdminUserActionException("Не можете да си махнете ADMIN ролята.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdminUserActionException("Потребителят не е намерен."));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new AdminUserActionException("Роля ADMIN липсва."));

        boolean hasAdmin = user.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));

        if (!hasAdmin) {
            return;
        }

        user.getRoles().remove(adminRole);
        userRepository.save(user);

        log.info("User {} revoked ADMIN by {}", user.getUsername(), admin.getUsername());
    }

    @Override
    @Transactional
    public UserProfileDto getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Потребителят не е намерен."));

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Override
    @Transactional
    public void updateProfile(String username, UserProfileDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Потребителят не е намерен."));

        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Имейлът вече е регистриран.");
        }

        user.setEmail(dto.getEmail());
        userRepository.save(user);

    }

    private User getUserByUsernameOrThrow(String username, String message) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AdminUserActionException(message));
    }

    private User getUserByIdOrThrow(UUID id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AdminUserActionException(message));
    }

    private void ensureAdmin(User admin) {
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getName()));
        if (!isAdmin) {
            throw new AdminUserActionException("Нямате права за тази операция.");
        }
    }


}

