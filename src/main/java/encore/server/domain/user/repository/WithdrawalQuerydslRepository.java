package encore.server.domain.user.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.comment.entity.QComment;
import encore.server.domain.comment.entity.QCommentLike;
import encore.server.domain.hashtag.entity.QPostHashtag;
import encore.server.domain.hashtag.entity.QUserHashtag;
import encore.server.domain.inquiry.entity.QInquiryAboutCategory;
import encore.server.domain.inquiry.entity.QInquiryAboutServiceUse;
import encore.server.domain.mission.entity.QMission;
import encore.server.domain.notification.entity.QNotification;
import encore.server.domain.post.entity.QPost;
import encore.server.domain.post.entity.QPostImage;
import encore.server.domain.post.entity.QPostLike;
import encore.server.domain.post.entity.QPostTerm;
import encore.server.domain.review.entity.QReview;
import encore.server.domain.review.entity.QReviewLike;
import encore.server.domain.review.entity.QReviewReport;
import encore.server.domain.review.entity.QReviewTags;
import encore.server.domain.review.entity.QUserReview;
import encore.server.domain.subscription.entity.QSubscription;
import encore.server.domain.ticket.entity.QTicket;
import encore.server.domain.ticket.entity.QTicketActor;
import encore.server.domain.user.entity.QFCMToken;
import encore.server.domain.user.entity.QPenaltyHistory;
import encore.server.domain.user.entity.QUser;
import encore.server.domain.user.entity.QUserKeyword;
import encore.server.domain.user.entity.QUserTermOfUse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WithdrawalQuerydslRepository {

  private final JPAQueryFactory qf;

  // Q-types (네 프로젝트에 있는 Q클래스명에 맞게 import)
  private final QUser user = QUser.user;

  private final QPost post = QPost.post;
  private final QComment comment = QComment.comment;

  private final QPostImage postImage = QPostImage.postImage;
  private final QPostHashtag postHashtag = QPostHashtag.postHashtag;
  private final QPostLike postLike = QPostLike.postLike;
  private final QCommentLike commentLike = QCommentLike.commentLike;

  private final QPostTerm postTerm = QPostTerm.postTerm;          // 조인 엔티티
  private final QTicketActor ticketActor = QTicketActor.ticketActor; // 조인 엔티티

  private final QTicket ticket = QTicket.ticket;

  private final QReview review = QReview.review;
  private final QReviewLike reviewLike = QReviewLike.reviewLike;
  private final QReviewReport reviewReport = QReviewReport.reviewReport;
  private final QReviewTags reviewTags = QReviewTags.reviewTags;    // 엔티티로 매핑돼 있다면
  private final QUserReview userReview = QUserReview.userReview;

  private final QSubscription subscription = QSubscription.subscription;

  private final QMission mission = QMission.mission;
  private final QNotification notification = QNotification.notification;
  private final QPenaltyHistory penaltyHistory = QPenaltyHistory.penaltyHistory;
  private final QInquiryAboutCategory inquiryAboutCategory = QInquiryAboutCategory.inquiryAboutCategory;
  private final QInquiryAboutServiceUse inquiryAboutServiceUse = QInquiryAboutServiceUse.inquiryAboutServiceUse;

  private final QUserHashtag userHashtag = QUserHashtag.userHashtag;
  private final QUserKeyword userKeyword = QUserKeyword.userKeyword;
  private final QUserTermOfUse userTermOfUse = QUserTermOfUse.userTermOfUse;

  private final QFCMToken fCMToken = QFCMToken.fCMToken; // 엔티티명/ Q타입명 확인 필요

  // --------- subqueries ---------
  private JPQLQuery<Long> usersPostIds(Long userId) {
    return JPAExpressions.select(post.id)
        .from(post)
        .where(post.user.id.eq(userId).and(post.deletedAt.isNull()));
  }

  private JPQLQuery<Long> usersCommentIds(Long userId) {
    return JPAExpressions.select(comment.id)
        .from(comment)
        .where(comment.user.id.eq(userId).and(comment.deletedAt.isNull()));
  }

  private JPQLQuery<Long> commentsOnUsersPosts(Long userId) {
    return JPAExpressions.select(comment.id)
        .from(comment)
        .where(comment.post.id.in(usersPostIds(userId)).and(comment.deletedAt.isNull()));
  }

  private JPQLQuery<Long> usersReviewIds(Long userId) {
    return JPAExpressions.select(review.id)
        .from(review)
        .where(review.user.id.eq(userId).and(review.deletedAt.isNull()));
  }

  private JPQLQuery<Long> usersTicketIds(Long userId) {
    return JPAExpressions.select(ticket.id)
        .from(ticket)
        .where(ticket.user.id.eq(userId).and(ticket.deletedAt.isNull()));
  }

  // =========================
  // 1) 게시판 번들
  // =========================
  public void withdrawBoard(Long userId, LocalDateTime now) {

    // post_image / post_hashtag / post_like soft delete
    qf.update(postImage)
        .set(postImage.deletedAt, now)
        .where(postImage.post.id.in(usersPostIds(userId)).and(postImage.deletedAt.isNull()))
        .execute();

    qf.update(postHashtag)
        .set(postHashtag.deletedAt, now)
        .where(postHashtag.post.id.in(usersPostIds(userId)).and(postHashtag.deletedAt.isNull()))
        .execute();

    qf.update(postLike)
        .set(postLike.deletedAt, now)
        .where(
            postLike.deletedAt.isNull().and(
                postLike.user.id.eq(userId)
                    .or(postLike.post.id.in(usersPostIds(userId)))
            )
        )
        .execute();


    qf.delete(postTerm)
        .where(postTerm.post.id.in(usersPostIds(userId)))
        .execute();

    // comment soft delete (내가 쓴 댓글 + 내 글에 달린 댓글)
    qf.update(comment)
        .set(comment.deletedAt, now)
        .where(comment.user.id.eq(userId).and(comment.deletedAt.isNull()))
        .execute();

    qf.update(comment)
        .set(comment.deletedAt, now)
        .where(comment.post.id.in(usersPostIds(userId)).and(comment.deletedAt.isNull()))
        .execute();

    // comment_like soft delete
    qf.update(commentLike)
        .set(commentLike.deletedAt, now)
        .where(commentLike.deletedAt.isNull().and(
            commentLike.user.id.eq(userId)
                .or(commentLike.comment.id.in(usersCommentIds(userId)))
                .or(commentLike.comment.id.in(commentsOnUsersPosts(userId)))
        ))
        .execute();

    // post soft delete (마지막)
    qf.update(post)
        .set(post.deletedAt, now)
        .where(post.user.id.eq(userId).and(post.deletedAt.isNull()))
        .execute();
  }

  // =========================
  // 2) 리뷰/티켓 번들
  // =========================
  public void withdrawReviewTicket(Long userId, LocalDateTime now) {

    // 순환/유니크 꼬임 예방: ticket.review nullify
    // (필드명이 review인지 reviewId인지에 따라 수정)
    qf.update(ticket)
        .setNull(ticket.review)  // <-- 만약 t.reviewId 같은 scalar면 setNull(t.reviewId)
        .where(ticket.user.id.eq(userId))
        .execute();

    // review_like: 내가 누른 것 + 내 리뷰에 달린 것
    qf.delete(reviewLike)
        .where(
            reviewLike.user.id.eq(userId)
                .or(reviewLike.review.id.in(usersReviewIds(userId)))
        )
        .execute();

    // review_report: 내가 신고한 것 + 내 리뷰가 신고당한 것
    qf.delete(reviewReport)
        .where(
            reviewReport.reporter.id.eq(userId)
                .or(reviewReport.review.id.in(usersReviewIds(userId)))
        )
        .execute();


    // review_tags: deletedAt 없으면 hard delete
    qf.delete(reviewTags)
        .where(reviewTags.review.id.in(usersReviewIds(userId)))
        .execute();

    // user_review: 내가 한 북마크 + 내 리뷰를 북마크한 것까지 soft delete
    qf.update(userReview)
        .set(userReview.deletedAt, now)
        .where(userReview.deletedAt.isNull().and(
            userReview.user.id.eq(userId)
                .or(userReview.review.id.in(usersReviewIds(userId)))
        ))
        .execute();

    // review soft delete
    qf.update(review)
        .set(review.deletedAt, now)
        .where(review.user.id.eq(userId).and(review.deletedAt.isNull()))
        .execute();


    // ticket_actors hard delete
    qf.delete(ticketActor)
        .where(ticketActor.ticket.id.in(usersTicketIds(userId)))
        .execute();

    // ticket soft delete
    qf.update(ticket)
        .set(ticket.deletedAt, now)
        .where(ticket.user.id.eq(userId).and(ticket.deletedAt.isNull()))
        .execute();
  }

  // =========================
  // 3) 기타 유저 데이터
  // =========================
  public void withdrawMisc(Long userId, LocalDateTime now) {
    qf.update(subscription)
        .set(subscription.deletedAt, now)
        .where(subscription.deletedAt.isNull().and(
            subscription.follower.id.eq(userId).or(subscription.following.id.eq(userId))
        ))
        .execute();

    // fcm token: 보통 hard delete
    qf.delete(fCMToken)
        .where(fCMToken.user.id.eq(userId))
        .execute();
    // notification: 탈퇴 유저의 알림 전부 삭제
    qf.delete(notification)
        .where(notification.user.id.eq(userId))
        .execute();

    // user_hashtag: 유저-해시태그 관계 전부 삭제
    qf.delete(userHashtag)
        .where(userHashtag.user.id.eq(userId))
        .execute();

    qf.update(mission).set(mission.deletedAt, now).where(mission.user.id.eq(userId).and(mission.deletedAt.isNull())).execute();
    qf.update(penaltyHistory).set(penaltyHistory.deletedAt, now).where(
        penaltyHistory.user.id.eq(userId).and(penaltyHistory.deletedAt.isNull())).execute();
    qf.update(inquiryAboutCategory).set(inquiryAboutCategory.deletedAt, now).where(
        inquiryAboutCategory.user.id.eq(userId).and(inquiryAboutCategory.deletedAt.isNull())).execute();
    qf.update(inquiryAboutServiceUse).set(inquiryAboutServiceUse.deletedAt, now).where(
        inquiryAboutServiceUse.user.id.eq(userId).and(inquiryAboutServiceUse.deletedAt.isNull())).execute();

    qf.update(userKeyword).set(userKeyword.deletedAt, now).where(
        userKeyword.user.id.eq(userId).and(userKeyword.deletedAt.isNull())).execute();
    qf.update(userTermOfUse).set(userTermOfUse.deletedAt, now).where(
        userTermOfUse.user.id.eq(userId).and(userTermOfUse.deletedAt.isNull())).execute();
  }

  // =========================
  // 4) user 익명화 + soft delete
  // =========================
  public void anonymizeAndDeleteUser(Long userId, LocalDateTime now) {
    qf.update(user)
        .set(user.deletedAt, now)
        .set(user.email, "deleted_" + userId + "@deleted.local")
        .set(user.nickName, "deleted_" + userId)
      //  .set(u.name, "탈퇴회원")
        .set(user.profileImageUrl, (String) null)
        .where(user.id.eq(userId).and(user.deletedAt.isNull()))
        .execute();
  }
}
