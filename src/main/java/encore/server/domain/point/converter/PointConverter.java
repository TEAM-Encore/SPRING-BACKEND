package encore.server.domain.point.converter;

import encore.server.domain.point.dto.response.PointBalanceRes;
import encore.server.domain.point.dto.response.PointHistoryRes;
import encore.server.domain.point.entity.PointHistory;
import encore.server.domain.user.entity.User;

public class PointConverter {

    public static PointHistoryRes toHistoryResponse(PointHistory pointHistory) {
        return PointHistoryRes.builder()
                .id(pointHistory.getId())
                .changeAmount(pointHistory.getChangeAmount())
                .balanceAfter(pointHistory.getBalanceAfter())
                .description(pointHistory.getDescription())
                .type(pointHistory.getType().name())
                .createdAt(pointHistory.getCreatedAt())
                .build();
    }

    public static PointBalanceRes toBalanceResponse(User user) {
        return PointBalanceRes.builder()
                .currentBalance(user.getPoint())
                .build();
    }
}
