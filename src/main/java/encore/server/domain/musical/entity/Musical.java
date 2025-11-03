package encore.server.domain.musical.entity;


import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "UNIQUE: title - location", columnNames = {"title", "location"})})
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Musical extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @Column(nullable = false, unique = true)
    private String openApiId;

    public void updateTitle(String title) { this.title = title;}

    public void updateLocation(String location) { this.location = location;}

    public void updateStartDate(LocalDate startDate) { this.startDate = startDate;}

    public void updateEndDate(LocalDate endDate) { this.endDate = endDate;}

    public void updateImageUrl(String imageUrl) { this.imageUrl = imageUrl;}
}
