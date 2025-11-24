package bg.softuni.alpinevillas.integration.review;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDto {

    private UUID id;
    private UUID villaId;
    private UUID reviewerId;
    private int rating;
    private String comment;
    private LocalDateTime createdOn;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVillaId() {
        return villaId;
    }

    public void setVillaId(UUID villaId) {
        this.villaId = villaId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
