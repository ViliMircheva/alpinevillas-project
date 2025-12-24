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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                           RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "auth/register";
        }

        userService.register(user);

        ra.addFlashAttribute("msg", "Успешна регистрация! Можеш да влезеш.");
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
