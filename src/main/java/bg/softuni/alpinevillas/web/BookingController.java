package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.BookingService;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.web.dto.BookingCreateDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final VillaService villaService;

    public BookingController(BookingService bookingService, VillaService villaService) {
        this.bookingService = bookingService;
        this.villaService = villaService;
    }

    @GetMapping("/add/{villaId}")
    public String addForm(@PathVariable UUID villaId, Model model) {
        model.addAttribute("villa", villaService.getDetails(villaId));
        if (!model.containsAttribute("booking")) {
            model.addAttribute("booking", new BookingCreateDto());
        }
        return "bookings/add";
    }

    @PostMapping("/add/{villaId}")
    public String add(@PathVariable UUID villaId,
                      @Valid @ModelAttribute("booking") BookingCreateDto booking,
                      BindingResult br,
                      @AuthenticationPrincipal(expression = "username") String username,
                      Model model) {

        if (username == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            model.addAttribute("villa", villaService.getDetails(villaId));
            return "bookings/add";
        }

        try {
            bookingService.createBooking(villaId, username, booking);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("villa", villaService.getDetails(villaId));
            model.addAttribute("error", ex.getMessage());
            return "bookings/add";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            model.addAttribute("villa", villaService.getDetails(villaId));
            model.addAttribute("error", "Проблем при запис в базата: " + ex.getMostSpecificCause().getMessage());
            return "bookings/add";
        }

        return "redirect:/bookings/mine";
    }

    @GetMapping("/mine")
    public String mine(@AuthenticationPrincipal(expression = "username") String username, Model model) {
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("bookings", bookingService.myBookings(username));
        return "bookings/mine";
    }

    @GetMapping("/owner")
    public String owner(@AuthenticationPrincipal(expression = "username") String username, Model model) {
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("bookings", bookingService.ownerBookings(username));
        return "bookings/owner";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id,
                         @AuthenticationPrincipal(expression = "username") String username,
                         RedirectAttributes ra) {
        try {
            bookingService.cancel(id, username);
            ra.addFlashAttribute("msg", "Резервацията е отменена.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/mine";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable UUID id,
                          @AuthenticationPrincipal(expression = "username") String ownerUsername,
                          RedirectAttributes ra) {
        try {
            bookingService.confirm(id, ownerUsername);
            ra.addFlashAttribute("msg", "Резервацията е потвърдена.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/owner";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable UUID id,
                          @AuthenticationPrincipal(expression = "username") String ownerUsername,
                          RedirectAttributes ra) {
        try {
            bookingService.decline(id, ownerUsername);
            ra.addFlashAttribute("msg", "Резервацията е отказана.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bookings/owner";
    }

}
