package encore.server.global.util.quartz;

import encore.server.domain.musical.service.MusicalSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicalUpdateJob extends QuartzJobBean {

    private final MusicalSyncService musicalSyncService;

    @Value("${kopis.service-key:}")
    private String serviceKey;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            if (serviceKey == null || serviceKey.isBlank()) {
                log.warn("[MusicalUpdateJob] kopis.service-key 미설정. 스킵");
                return;
            }
            log.info("[MusicalUpdateJob] 일일 갱신 시작");
            musicalSyncService.updateDaily(serviceKey);
            log.info("[MusicalUpdateJob] 일일 갱신 종료");
        } catch (Exception e) {
            log.error("[MusicalUpdateJob] 실행 중 오류", e);
        }
    }
}
