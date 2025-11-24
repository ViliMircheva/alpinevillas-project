package bg.softuni.alpinevillas.integration.review;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "review-service", url = "${review.service.url}")
public interface ReviewClient {

    @GetMapping("/api/reviews/{villaId}")
    List<ReviewDto> getReviews(@PathVariable("villaId") UUID villaId);

    @PostMapping("/api/reviews")
    UUID createReview(@RequestBody ReviewCreateDto dto);

    @DeleteMapping("/api/reviews/{id}")
    void deleteReview(@PathVariable("id") UUID id);

    @DeleteMapping("/api/reviews/villa/{villaId}")
    void deleteReviewsByVilla(@PathVariable("villaId") UUID villaId);


}
