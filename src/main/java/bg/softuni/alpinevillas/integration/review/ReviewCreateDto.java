package bg.softuni.alpinevillas.integration.review;

import java.util.UUID;

public class ReviewCreateDto {

    private UUID villaId;
    private UUID reviewerId;
    private int rating;
    private String comment;

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
}
