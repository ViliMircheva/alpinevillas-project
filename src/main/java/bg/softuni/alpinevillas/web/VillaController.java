package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.repositories.AmenityRepository;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import bg.softuni.alpinevillas.web.dto.VillaEditDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/villas")
public class VillaController {

    private final VillaService villaService;
    private final AmenityRepository amenityRepository;

    public VillaController(VillaService villaService, AmenityRepository amenityRepository) {
        this.villaService = villaService;
        this.amenityRepository = amenityRepository;
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
    public String addVilla(@Valid @ModelAttribute("villa") VillaCreateDto dto,
                           BindingResult br,
                           @AuthenticationPrincipal(expression = "username") String username,
                           Model model) {

        if (br.hasErrors()) {
            model.addAttribute("amenities", amenityRepository.findAll());
            return "villas/add";
        }
        UUID id = villaService.createVilla(dto, username);
        return "redirect:/villas/" + id;
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id,
                           @AuthenticationPrincipal(expression = "username") String username,
                           Model model) {

        VillaEditDto dto = villaService.getEditData(id, username);
        model.addAttribute("villa", dto);
        model.addAttribute("amenities", amenityRepository.findAll());
        return "villas/edit";
    }

    @PostMapping("/edit/{id}")
    public String editVilla(@PathVariable UUID id,
                            @Valid @ModelAttribute("villa") VillaEditDto dto,
                            BindingResult br,
                            @AuthenticationPrincipal(expression = "username") String username,
                            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("amenities", amenityRepository.findAll());
            return "villas/edit";
        }

        dto.setId(id);
        villaService.editVilla(dto, username);
        return "redirect:/villas/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteVilla(@PathVariable UUID id,
                              @AuthenticationPrincipal(expression = "username") String username) {

        villaService.deleteVilla(id, username);
        return "redirect:/villas";
    }
}
