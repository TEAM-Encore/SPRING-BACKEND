package encore.server.domain.musical.controller;

import encore.server.domain.musical.service.MusicalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/musicals/sync")
@RequiredArgsConstructor
public class MusicalSyncAdminController {

    private final MusicalSyncService musicalSyncService;

    /**
     * 초기 적재 (운영자 수동 실행)
     */
    @PostMapping("/initial")
    public String initialLoad(@RequestParam String serviceKey) {
        musicalSyncService.initialLoad(serviceKey);
        return "초기 적재 완료";
    }

    /**
     * 일일 업데이트 (수동 실행 or 스케줄러 병행 가능)
     */
    @PostMapping("/daily")
    public String updateDaily(@RequestParam String serviceKey) {
        musicalSyncService.updateDaily(serviceKey);
        return "일일 갱신 완료";
    }
}
