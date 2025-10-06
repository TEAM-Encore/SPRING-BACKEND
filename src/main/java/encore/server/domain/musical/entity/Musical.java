package encore.server.domain.musical.entity;


import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
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
    private LocalDate startDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "varchar(500)")
    private String location;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @Column(nullable = false, unique = true)
    private String openApiId;

    @Builder
    public Musical(String title, LocalDate startDate, LocalDate endDate, String location, String imageUrl, String openApiId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.imageUrl = imageUrl;
        this.openApiId = openApiId;
    }


    public void updateTitle(String title) { this.title = title;}

    public void updateLocation(String location) { this.location = location;}

    public void updateStartDate(LocalDate startDate) { this.startDate = startDate;}

    public void updateEndDate(LocalDate endDate) { this.endDate = endDate;}

    public void updateImageUrl(String imageUrl) { this.imageUrl = imageUrl;}


}
