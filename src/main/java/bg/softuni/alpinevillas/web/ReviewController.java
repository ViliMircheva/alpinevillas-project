package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.integration.review.ReviewClient;
import bg.softuni.alpinevillas.integration.review.ReviewCreateDto;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.service.VillaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/villas")
public class ReviewController {

    private final ReviewClient reviewClient;
    private final UserRepository userRepository;
    private final VillaService villaService;

    public ReviewController(ReviewClient reviewClient,
                            UserRepository userRepository, VillaService villaService) {
        this.reviewClient = reviewClient;
        this.userRepository = userRepository;
        this.villaService = villaService;
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable("id") UUID villaId,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            @AuthenticationPrincipal Object principal,
                            RedirectAttributes ra) {

        UUID reviewerId = null;

        if (principal instanceof bg.softuni.alpinevillas.entities.User u) {
            reviewerId = u.getId();

        } else if (principal instanceof org.springframework.security.core.userdetails.User su) {
            Optional<User> optUser = userRepository.findByUsername(su.getUsername());
            if (optUser.isPresent()) {
                reviewerId = optUser.get().getId();
            }
        }

        if (reviewerId == null) {
            ra.addFlashAttribute("error", "Моля, влезте в профила си, за да оставите отзив.");
            return "redirect:/villas/" + villaId;
        }

        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setVillaId(villaId);
        dto.setReviewerId(reviewerId);
        dto.setRating(rating);
        dto.setComment(comment);

        try {
            reviewClient.createReview(dto);
            villaService.evictVillaCache(villaId);

            ra.addFlashAttribute("msg", "Благодарим за отзива! 🌟");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Възникна грешка при записване на отзива.");
        }

        return "redirect:/villas/" + villaId;
    }

    @PostMapping("/{villaId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable("villaId") UUID villaId,
                               @PathVariable("reviewId") UUID reviewId,
                               RedirectAttributes ra) {
        try {
            reviewClient.deleteReview(reviewId);
            ra.addFlashAttribute("msg", "Отзивът беше изтрит успешно.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Възникна грешка при изтриване на отзива.");
        }

        return "redirect:/villas/" + villaId;
    }
}
