package encore.server.domain.musical.entity;


import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(nullable = false, columnDefinition = "bigint")
    private Long age;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long series;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "musical_show_times", joinColumns = @JoinColumn(name = "musical_id"))
    @Column(name = "show_time")
    private List<String> showTimes = new ArrayList<>();

    @OneToMany(mappedBy = "musical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MusicalActor> musicalActors = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isFeatured; // 이달의 인기 뮤지컬 여부

}
