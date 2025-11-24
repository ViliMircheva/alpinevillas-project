package bg.softuni.alpinevillas.service;

import bg.softuni.alpinevillas.web.dto.RegistrationDto;

public interface UserService {
    void register(RegistrationDto dto);
    void blockUser(String username, String adminName);
}
