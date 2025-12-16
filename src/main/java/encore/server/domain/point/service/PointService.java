package encore.server.domain.point.service;

import encore.server.domain.point.converter.PointConverter;
import encore.server.domain.point.dto.response.PointBalanceRes;
import encore.server.domain.point.dto.response.PointHistoryRes;
import encore.server.domain.point.entity.PointHistory;
import encore.server.domain.point.enumerate.PointType;
import encore.server.domain.point.repository.PointHistoryRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 포인트 적립
     */
    @Transactional
    public void earnPoints(Long userId, Long amount, String description, PointType type, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Long balanceAfter = user.addPoint(amount);

        PointHistory history = PointHistory.builder()
                .user(user)
                .changeAmount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .type(type)
                .relatedId(relatedId)
                .build();

        pointHistoryRepository.save(history);
    }

    /**
     * 포인트 사용
     */
    @Transactional
    public void usePoints(Long userId, Long amount, String description, PointType type, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (user.getPoint() < amount) {
            throw new ApplicationException(ErrorCode.POINT_NOT_ENOUGH_EXCEPTION);
        }

        Long balanceAfter = user.usePoint(amount);

        PointHistory history = PointHistory.builder()
                .user(user)
                .changeAmount(-amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .type(type)
                .relatedId(relatedId)
                .build();

        pointHistoryRepository.save(history);
    }

    /**
     * 하루 한 번 포인트 적립 (좋아요용)
     */
    @Transactional
    public void earnDailyPoints(Long userId, Long amount, String description, PointType type, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // 오늘 이미 적립했는지 확인
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        boolean alreadyEarnedToday = pointHistoryRepository.existsTodayByUserAndType(
                user, type, startOfDay, endOfDay
        );

        if (alreadyEarnedToday) {
            // 이미 오늘 적립했으면 종료
            return;
        }

        // 포인트 적립
        Long balanceAfter = user.addPoint(amount);

        PointHistory history = PointHistory.builder()
                .user(user)
                .changeAmount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .type(type)
                .relatedId(relatedId)
                .build();

        pointHistoryRepository.save(history);
    }

    /**
     * 포인트 히스토리 조회 (페이징)
     */
    public Page<PointHistoryRes> getPointHistory(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Page<PointHistory> histories = pointHistoryRepository
                .findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user, pageable);

        return histories.map(PointConverter::toHistoryResponse);
    }

    /**
     * 포인트 잔액 조회
     */
    public PointBalanceRes getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return PointConverter.toBalanceResponse(user);
    }
}
