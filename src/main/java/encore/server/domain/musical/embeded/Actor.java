package encore.server.domain.musical.embeded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Actor {
    @Column(columnDefinition = "varchar(500)")
    private String actor1;

    @Column(columnDefinition = "varchar(500)")
    private String actor2;

    @Column(columnDefinition = "varchar(500)")
    private String actor3;

    @Column(columnDefinition = "text")
    private String actorUrl1;

    @Column(columnDefinition = "text")
    private String actorUrl2;

    @Column(columnDefinition = "text")
    private String actorUrl3;

}