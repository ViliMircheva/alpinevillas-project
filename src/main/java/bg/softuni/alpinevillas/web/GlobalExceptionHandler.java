package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.exception.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("title", "Не е намерено");
        model.addAttribute("message", ex.getMessage());
        return "errors/simple";
    }

    @ExceptionHandler({ForbiddenOperationException.class})
    public String handleForbidden(RuntimeException ex, Model model) {
        model.addAttribute("title", "Достъп отказан");
        model.addAttribute("message", ex.getMessage());
        return "errors/simple";
    }

    @ExceptionHandler({OverlappingBookingException.class})
    public String handleBadRequest(RuntimeException ex, Model model) {
        model.addAttribute("title", "Невалидна операция");
        model.addAttribute("message", ex.getMessage());
        return "errors/simple";
    }

    @ExceptionHandler(AdminUserActionException.class)
    public String handleAdminUserAction(AdminUserActionException ex,
                                        RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/admin/users";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("title", "Невалидни данни");
        model.addAttribute("message", ex.getMessage());
        return "errors/simple";
    }


    @ExceptionHandler(Exception.class)
    public String handleOther(Exception ex, Model model) {
        model.addAttribute("title", "Грешка");
        model.addAttribute("message", "Възникна неочаквана грешка.");
        return "errors/simple";
    }

    @ExceptionHandler(BookingMineActionException.class)
    public String handleBookingMineAction(BookingMineActionException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/bookings/mine";
    }

    @ExceptionHandler(BookingOwnerActionException.class)
    public String handleBookingOwnerAction(BookingOwnerActionException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/bookings/owner";
    }

    @ExceptionHandler(BookingCreateException.class)
    public String handleBookingCreate(BookingCreateException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/bookings/add/" + ex.getVillaId();
    }

    @ExceptionHandler(RegistrationException.class)
    public String handleRegistration(RegistrationException ex,
                                     RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(VillaCreateException.class)
    public String handleVillaCreate(VillaCreateException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        ra.addFlashAttribute("villa", ex.getDto()); // да ти останат попълнените полета
        return "redirect:/villas/add";
    }

    @ExceptionHandler(VillaEditException.class)
    public String handleVillaEdit(VillaEditException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/villas/edit/" + ex.getVillaId();
    }

    @ExceptionHandler(ReviewCreateException.class)
    public String handleReviewCreate(ReviewCreateException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/villas/" + ex.getVillaId();
    }

    @ExceptionHandler(ReviewDeleteException.class)
    public String handleReviewDelete(ReviewDeleteException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/villas/" + ex.getVillaId();
    }



}
