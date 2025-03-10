package encore.server.domain.musical.entity;


import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SQLDelete(sql = "UPDATE musical SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Musical extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @Column(nullable = false, columnDefinition = "varchar(500)")
    private String location;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long runningTime;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String age;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long series;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @OneToMany(mappedBy = "musical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowTime> showTimes = new ArrayList<>();

    @OneToMany(mappedBy = "musical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MusicalActor> musicalActors = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isFeatured; // 이달의 인기 뮤지컬 여부

    @Column(columnDefinition = "varchar(255)")
    private String interparkId;

    @Builder
    public Musical(String title, LocalDateTime startDate, LocalDateTime endDate, String location, Long runningTime, String age, Long series, String imageUrl, List<ShowTime> showTimes, List<MusicalActor> musicalActors, boolean isFeatured, String interparkId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.runningTime = runningTime;
        this.age = age;
        this.series = series != null ? series : 1L;
        this.imageUrl = imageUrl;
        this.showTimes = showTimes != null ? showTimes : new ArrayList<>();
        this.musicalActors = musicalActors != null ? musicalActors : new ArrayList<>();
        this.isFeatured = isFeatured;
        this.interparkId = interparkId;
    }

    public void addMusicalActors(MusicalActor musicalActor) {
        this.musicalActors.add(musicalActor);
    }
    public void addShowTime(ShowTime showTime) {
        this.showTimes.add(showTime);
    }
    public void updateTitle(String title) { this.title = title;}

    public void updateSeries(Long series) { this.series = series;}

    public void updateLocation(String location) { this.location = location;}

    public void updateStartDate(LocalDateTime startDate) { this.startDate = startDate;}

    public void updateEndDate(LocalDateTime endDate) { this.endDate = endDate;}

    public void updateRunningTime(Long runningTime) { this.runningTime = runningTime;}

    public void updateAge(String age) { this.age = age;}

    public void updateImageUrl(String imageUrl) { this.imageUrl = imageUrl;}

    public void updateIsFeatured(boolean isFeatured) { this.isFeatured = isFeatured;}

    public void clearMusicalActors() { this.musicalActors.clear();}

    public void clearShowTimes() { this.showTimes.clear();}

}
