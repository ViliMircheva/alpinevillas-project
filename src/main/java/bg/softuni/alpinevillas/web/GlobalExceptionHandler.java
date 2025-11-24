package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.service.exception.ForbiddenOperationException;
import bg.softuni.alpinevillas.service.exception.NotFoundException;
import bg.softuni.alpinevillas.service.exception.OverlappingBookingException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler({OverlappingBookingException.class, IllegalArgumentException.class})
    public String handleBadRequest(RuntimeException ex, Model model) {
        model.addAttribute("title", "Невалидна операция");
        model.addAttribute("message", ex.getMessage());
        return "errors/simple";
    }

    @ExceptionHandler(Exception.class)
    public String handleOther(Exception ex, Model model) {
        model.addAttribute("title", "Грешка");
        model.addAttribute("message", "Възникна неочаквана грешка.");
        return "errors/simple";
    }
}
