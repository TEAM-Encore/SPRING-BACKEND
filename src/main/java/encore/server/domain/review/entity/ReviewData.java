package encore.server.domain.review.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewData {

    private View view;
    private Sound sound;
    private Facility facility;
    private Rating rating;

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class View {
        private Long viewLevel;
        private String viewReview;

        @Builder
        public View(Long viewLevel, String viewReview) {
            this.viewLevel = viewLevel;
            this.viewReview = viewReview;
        }
    }

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Sound {
        private Long soundLevel;
        private String soundReview;

        @Builder
        public Sound(Long soundLevel, String soundReview) {
            this.soundLevel = soundLevel;
            this.soundReview = soundReview;
        }
    }

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Facility {
        private Long facilityLevel;
        private String facilityReview;

        @Builder
        public Facility(Long facilityLevel, String facilityReview) {
            this.facilityLevel = facilityLevel;
            this.facilityReview = facilityReview;
        }
    }

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Rating {
        private Long numberRating;
        private Long storyRating;
        private Long revisitRating;
        private Long actorRating;
        private Long performanceRating;
        private Float totalRating;
        private String ratingReview;

        @Builder
        public Rating(Long numberRating, Long storyRating, Long revisitRating, Long actorRating, Long performanceRating, Float totalRating, String ratingReview) {
            this.numberRating = numberRating;
            this.storyRating = storyRating;
            this.revisitRating = revisitRating;
            this.actorRating = actorRating;
            this.performanceRating = performanceRating;
            this.totalRating = totalRating;
            this.ratingReview = ratingReview;
        }
    }

    @Builder
    public ReviewData(View view, Sound sound, Facility facility, Rating rating) {
        this.view = view;
        this.sound = sound;
        this.facility = facility;
        this.rating = rating;
    }
}