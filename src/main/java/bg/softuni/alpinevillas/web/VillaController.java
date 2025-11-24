package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.integration.review.ReviewClient;
import bg.softuni.alpinevillas.repositories.AmenityRepository;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/villas")
public class VillaController {

    private final VillaService villaService;
    private final AmenityRepository amenityRepository;
    private final ReviewClient reviewClient;

    public VillaController(VillaService villaService, AmenityRepository amenityRepository, ReviewClient reviewClient) {
        this.villaService = villaService;
        this.amenityRepository = amenityRepository;
        this.reviewClient = reviewClient;
    }

    @GetMapping
    public String catalog(Model model) {
        model.addAttribute("villas", villaService.listAll());
        return "villas/catalog";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        model.addAttribute("villa", villaService.getDetails(id));
        return "villas/details";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("villa")) {
            model.addAttribute("villa", new VillaCreateDto());
        }
        model.addAttribute("amenities", amenityRepository.findAll());
        return "villas/add";
    }

    @PostMapping("/add")
    public String addVilla(
            @Valid @ModelAttribute("villa") VillaCreateDto dto,
            BindingResult br,
            @AuthenticationPrincipal Object principal,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allAmenities", amenityRepository.findAll());
            return "villas/add";
        }

        String username = null;
        if (principal instanceof User u) {
            username = u.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User su) {
            username = su.getUsername();
        }

        if (username == null) {
            br.reject("auth", "Няма активен потребител");
            model.addAttribute("allAmenities", amenityRepository.findAll());
            return "villas/add";
        }

        try {
            UUID id = villaService.createVilla(dto, username);
            return "redirect:/villas/" + id;
        }
        catch (RuntimeException ex) {
            br.reject("create", ex.getMessage() != null ? ex.getMessage() : "Грешка при запис в базата.");
            model.addAttribute("allAmenities", amenityRepository.findAll());
            return "villas/add";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVilla(@PathVariable UUID id,
                              @AuthenticationPrincipal Object principal,
                              RedirectAttributes ra) {

        String username = null;

        if (principal instanceof User u) {
            username = u.getUsername();
        }
        else if (principal instanceof org.springframework.security.core.userdetails.User su) {
            username = su.getUsername();
        }

        try {
            villaService.deleteVilla(id, username);
            ra.addFlashAttribute("msg", "Вилата е изтрита успешно.");
        }
        catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/villas";
    }
}
