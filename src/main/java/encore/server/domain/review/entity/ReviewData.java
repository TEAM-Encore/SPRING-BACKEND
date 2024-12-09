package encore.server.domain.review.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class ReviewData {

    private View view;
    private Sound sound;
    private Facility facility;
    private Rating rating;

    @Getter
    @Embeddable
    public static class View {
        private Long viewLevel;
        private String viewReview;
    }

    @Getter
    @Embeddable
    public static class Sound {
        private Long soundLevel;
        private String soundReview;
    }

    @Getter
    @Embeddable
    public static class Facility {
        private Long facilityLevel;
        private String facilityReview;
    }

    @Getter
    @Embeddable
    public static class Rating {
        private Long numberRating;
        private Long storyRating;
        private Long revisitRating;
        private Long actorRating;
        private Long performanceRating;
        private Float totalRating;
        private String ratingReview;
    }
}