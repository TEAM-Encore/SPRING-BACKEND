package encore.server.domain.inquiry.entity;

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
@SQLDelete(sql = "UPDATE inquiry_about_service_use SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryAboutServiceUse extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false, columnDefinition = "varchar(255)")
  private String emailToReceive;

  @Column(columnDefinition = "TEXT")
  private String imageURL;
  private Boolean isProcessed;

  @Builder
  public InquiryAboutServiceUse(User user, String content, String emailToReceive, String imageURL,
      Boolean isProcessed) {
    this.user = user;
    this.content = content;
    this.emailToReceive = emailToReceive;
    this.imageURL = imageURL;
    this.isProcessed = isProcessed;
  }
}
