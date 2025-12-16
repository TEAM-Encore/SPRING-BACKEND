package encore.server.domain.point.repository;

import encore.server.domain.point.entity.PointHistory;
import encore.server.domain.point.enumerate.PointType;
import encore.server.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // 포인트 히스토리 목록 조회 (페이징)
    Page<PointHistory> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user, Pageable pageable);

    // 포인트 히스토리 목록 조회 (전체)
    List<PointHistory> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user);

    // 오늘 특정 타입의 포인트 적립 여부 확인 (하루 한 번 체크용)
    @Query("SELECT COUNT(ph) > 0 FROM PointHistory ph " +
           "WHERE ph.user = :user " +
           "AND ph.type = :type " +
           "AND ph.createdAt >= :startOfDay " +
           "AND ph.createdAt < :endOfDay " +
           "AND ph.deletedAt IS NULL")
    boolean existsTodayByUserAndType(@Param("user") User user,
                                      @Param("type") PointType type,
                                      @Param("startOfDay") LocalDateTime startOfDay,
                                      @Param("endOfDay") LocalDateTime endOfDay);
}
