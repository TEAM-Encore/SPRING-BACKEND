package encore.server.domain.musical.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", nullable = false)
    private Musical musical;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String day;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String time;

    @Builder
    public ShowTime(Musical musical, String day, String time) {
        this.musical = musical;
        this.day = day;
        this.time = time;
    }
}
