package encore.server.domain.user.entity;

import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.enumerate.UserRole;
import encore.server.domain.user.enumerate.ViewingFrequency;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Entity
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, columnDefinition = "bigint")
  private Long id;

  @Column(nullable = false, columnDefinition = "varchar(255)", unique = true)
  private String email;

  @Column(nullable = false, columnDefinition = "varchar(255)")
  private String password;

  @Column(nullable = false, columnDefinition = "varchar(50)")
  private String name;

  @Column(nullable = false, columnDefinition = "varchar(255)", unique = true)
  private String nickName;

  @Column(nullable = false, columnDefinition = "varchar(255)")
  @Enumerated(EnumType.STRING)
  private AuthProvider authProvider;

  @Column(nullable = false, columnDefinition = "varchar(255)")
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(nullable = true, columnDefinition = "varchar(255)")
  @Enumerated(EnumType.STRING)
  private ViewingFrequency viewingFrequency;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserKeyword> userPreferredKeywords = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserTermOfUse> userTermOfUses = new ArrayList<>();

  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long point;

  @Column(columnDefinition = "TEXT")
  private String profileImageUrl;

  @Builder
  public User(String nickName, Long point) {
    this.nickName = nickName;
    this.point = point;
  }

  public Long usePoint(Long point) {
    this.point -= point;
    return this.point;
  }

  public Long addPoint(Long point) {
    this.point += point;
    return this.point;
  }

  public void updateNickname(String nickName) {
    this.nickName = nickName;
  }

  public void updateProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void updateViewingFrequency(ViewingFrequency viewingFrequency) {
    this.viewingFrequency = viewingFrequency;
  }

  public void addUserPreferredKeywords(List<PreferredKeyword> preferredKeywords) {
    for (PreferredKeyword keyword : preferredKeywords) {
      UserKeyword userKeyword = UserKeyword.create(this, keyword); // 새로운 연관 엔티티 생성
      this.userPreferredKeywords.add(userKeyword);
    }
  }

  public void addUserTermOfUses(List<TermOfUse> termOfUses) {
    for (TermOfUse termOfUse : termOfUses) {
      UserTermOfUse userTermOfUse = UserTermOfUse.create(this, termOfUse);
      this.userTermOfUses.add(userTermOfUse);
    }
  }
}
