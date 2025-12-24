package bg.softuni.alpinevillas.web;

import bg.softuni.alpinevillas.integration.review.ReviewClient;
import bg.softuni.alpinevillas.integration.review.ReviewCreateDto;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.service.exception.ForbiddenOperationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/villas")
public class ReviewController {

    private final ReviewClient reviewClient;
    private final UserRepository userRepository;
    private final VillaService villaService;

    public ReviewController(ReviewClient reviewClient,
                            UserRepository userRepository,
                            VillaService villaService) {
        this.reviewClient = reviewClient;
        this.userRepository = userRepository;
        this.villaService = villaService;
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable UUID id,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            @AuthenticationPrincipal(expression = "username") String username) {

        if (username == null) {
            throw new ForbiddenOperationException("Моля, влезте в профила си, за да оставите отзив.");
        }

        UUID reviewerId = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен."))
                .getId();

        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setVillaId(id);
        dto.setReviewerId(reviewerId);
        dto.setRating(rating);
        dto.setComment(comment);

        reviewClient.createReview(dto);
        villaService.evictVillaCache(id);

        return "redirect:/villas/" + id;
    }

    @PostMapping("/{villaId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable UUID villaId,
                               @PathVariable UUID reviewId,
                               @AuthenticationPrincipal(expression = "username") String username) {
        reviewClient.deleteReview(reviewId);
        villaService.evictVillaCache(villaId);

        return "redirect:/villas/" + villaId;
    }
}
