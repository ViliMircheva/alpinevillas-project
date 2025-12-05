package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.web.dto.RegistrationDto;
import bg.softuni.alpinevillas.web.dto.UserAdminViewDto;
import bg.softuni.alpinevillas.web.dto.UserProfileDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void register(RegistrationDto dto);

    List<UserAdminViewDto> getAllUsersForAdmin();

    void blockUser(UUID userId, String adminUsername);

    void unblockUser(UUID userId, String adminUsername);

    void grantAdmin(UUID userId, String adminUsername);

    void revokeAdmin(UUID userId, String adminUsername);

    UserProfileDto getProfile(String username);

    void updateProfile(String username, UserProfileDto dto);

}
