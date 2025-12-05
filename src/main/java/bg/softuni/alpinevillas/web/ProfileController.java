package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.UserService;
import bg.softuni.alpinevillas.web.dto.UserProfileDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal(expression = "username") String username,
                          Model model) {
        if (username == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("profile")) {
            UserProfileDto dto = userService.getProfile(username);
            model.addAttribute("profile", dto);
        }

        return "user/profile";
    }

    @PostMapping
    public String update(@AuthenticationPrincipal(expression = "username") String username,
                         @Valid @ModelAttribute("profile") UserProfileDto profile,
                         BindingResult br,
                         RedirectAttributes ra) {

        if (username == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.profile", br);
            ra.addFlashAttribute("profile", profile);
            return "redirect:/profile";
        }

        try {
            userService.updateProfile(username, profile);
            ra.addFlashAttribute("msg", "Профилът е обновен.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("profile", profile);
        }

        return "redirect:/profile";
    }


}
