package encore.server.domain.post.entity;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.term.entity.Term;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "bigint")
    private User user;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private Boolean isNotice;

    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private Boolean isTemporarySave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private Category category;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostImage> postImages = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long likeCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long commentCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Term> terms = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content,Boolean isNotice, Boolean isTemporarySave, PostType postType,
                Category category, List<PostHashtag> postHashtags, List<PostImage> postImages, Long likeCount, Long commentCount) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isNotice = isNotice;
        this.isTemporarySave = isTemporarySave;
        this.postType = postType;
        this.category = category;
        this.postHashtags = postHashtags;
        this.postImages = postImages;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // likeCount 값을 갱신할 수 있는 setter 메서드 추가 **얘 추가함!!
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }


    public void addMusicalTerm(Term musicalTerm) {
        this.terms.add(musicalTerm);
        musicalTerm.setPost(this);
    }

    public void addMusicalTerms(List<Term> terms) {
        terms.forEach(this::addMusicalTerm);
    }

    public void updateContent(String updatedContent) {
        this.content = updatedContent;
    }

    public void updateCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
