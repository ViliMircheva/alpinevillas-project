package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.BookingService;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.service.exception.BookingMineActionException;
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
        if (br.hasErrors()) {
            model.addAttribute("villa", villaService.getDetails(villaId));
            return "bookings/add";
        }

        bookingService.createBooking(villaId, username, booking);
        return "redirect:/bookings/mine";
    }


    @GetMapping("/mine")
    public String mine(@AuthenticationPrincipal(expression = "username") String username, Model model) {
        model.addAttribute("bookings", bookingService.myBookings(username));
        return "bookings/mine";
    }

    @GetMapping("/owner")
    public String owner(@AuthenticationPrincipal(expression = "username") String username, Model model) {
        model.addAttribute("bookings", bookingService.ownerBookings(username));
        return "bookings/owner";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id,
                         @AuthenticationPrincipal(expression = "username") String username,
                         RedirectAttributes ra) {
        try {
            bookingService.cancel(id, username);
            ra.addFlashAttribute("successMsg", "Резервацията е отменена успешно.");
        } catch (BookingMineActionException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Възникна неочаквана грешка.");
        }
        return "redirect:/bookings/mine";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable UUID id,
                          @AuthenticationPrincipal(expression = "username") String ownerUsername,
                          RedirectAttributes ra) {

        bookingService.confirm(id, ownerUsername);
        ra.addFlashAttribute("msg", "Резервацията е потвърдена.");
        return "redirect:/bookings/owner";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable UUID id,
                          @AuthenticationPrincipal(expression = "username") String ownerUsername,
                          RedirectAttributes ra) {

        bookingService.decline(id, ownerUsername);
        ra.addFlashAttribute("msg", "Резервацията е отказана.");
        return "redirect:/bookings/owner";
    }

}
