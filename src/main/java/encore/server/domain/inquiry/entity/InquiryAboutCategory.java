package encore.server.domain.inquiry.entity;

import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@SQLDelete(sql = "UPDATE inquiry_about_category SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryAboutCategory extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String reason;

  @Column(nullable = false)
  private PostType requestPostType;

  @Column(nullable = false, columnDefinition = "varchar(255)")
  private String requestCategoryName;
  private Boolean isProcessed;

  @Builder
  public InquiryAboutCategory(User user, String reason, PostType requestPostType,
      String requestCategoryName, Boolean isProcessed) {
    this.user = user;
    this.reason = reason;
    this.requestPostType = requestPostType;
    this.requestCategoryName = requestCategoryName;
    this.isProcessed = isProcessed;
  }
}
