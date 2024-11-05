package encore.server.domain.hashtag.entity;

import encore.server.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false, columnDefinition = "bigint")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id", nullable = false, columnDefinition = "bigint")
        private Post post;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "hashtag_id", nullable = false, columnDefinition = "bigint")
        private Hashtag hashtag;

        @Builder
        public PostHashtag(Long id, Post post, Hashtag hashtag) {
            this.id = id;
            this.post = post;
            this.hashtag = hashtag;
        }
}
