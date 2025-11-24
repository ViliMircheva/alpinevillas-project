package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.UserService;
import bg.softuni.alpinevillas.web.dto.RegistrationDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new RegistrationDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegistrationDto user,
                           BindingResult br,
                           Model model) {
        if (br.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(user);
            model.addAttribute("msg", "Успешна регистрация! Можеш да влезеш.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (RuntimeException ex) {
            model.addAttribute("error", "Нещо се обърка. Опитай пак.");
        }
        return "auth/register";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
