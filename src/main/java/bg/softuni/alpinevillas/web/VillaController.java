package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.integration.review.ReviewClient;
import bg.softuni.alpinevillas.repositories.AmenityRepository;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import bg.softuni.alpinevillas.web.dto.VillaDetailsDto;
import bg.softuni.alpinevillas.web.dto.VillaEditDto;
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


    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id,
                           @AuthenticationPrincipal(expression = "username") String username,
                           Model model) {

        VillaDetailsDto villa = villaService.getDetails(id);

        if (!villa.getOwnerUsername().equals(username)) {
            return "redirect:/villas/" + id;
        }

        VillaEditDto dto = new VillaEditDto();
        dto.setId(villa.getId());
        dto.setName(villa.getName());
        dto.setRegion(villa.getRegion());
        dto.setCapacity(villa.getCapacity());
        dto.setPricePerNight(villa.getPricePerNight());
        dto.setDescription(villa.getDescription());
        dto.setImageUrl(villa.getImageUrl());
        dto.setAmenityIds(null);

        model.addAttribute("villa", dto);
        model.addAttribute("amenities", amenityRepository.findAll());

        return "villas/edit";
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

    @PostMapping("/edit/{id}")
    public String editVilla(@PathVariable UUID id,
                            @Valid @ModelAttribute("villa") VillaEditDto dto,
                            BindingResult br,
                            @AuthenticationPrincipal(expression = "username") String username,
                            Model model,
                            RedirectAttributes ra) {

        if (br.hasErrors()) {
            model.addAttribute("amenities", amenityRepository.findAll());
            return "villas/edit";
        }

        try {
            villaService.editVilla(dto, username);
            ra.addFlashAttribute("msg", "Промените са запазени успешно!");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/villas/" + id;
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
