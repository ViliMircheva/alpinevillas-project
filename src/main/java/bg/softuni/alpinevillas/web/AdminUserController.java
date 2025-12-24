package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.UserService;
import bg.softuni.alpinevillas.web.dto.UserAdminViewDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<UserAdminViewDto> users = userService.getAllUsersForAdmin();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable UUID id,
                            @AuthenticationPrincipal UserDetails admin) {
        userService.blockUser(id, admin.getUsername());
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unblock")
    public String unblockUser(@PathVariable UUID id,
                              @AuthenticationPrincipal UserDetails admin) {
        userService.unblockUser(id, admin.getUsername());
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/grant-admin")
    public String grantAdmin(@PathVariable UUID id,
                             @AuthenticationPrincipal UserDetails admin) {
        userService.grantAdmin(id, admin.getUsername());
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/revoke-admin")
    public String revokeAdmin(@PathVariable UUID id,
                              @AuthenticationPrincipal UserDetails admin) {
        userService.revokeAdmin(id, admin.getUsername());
        return "redirect:/admin/users";
    }
}
