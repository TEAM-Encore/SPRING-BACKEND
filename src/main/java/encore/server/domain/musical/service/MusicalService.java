package encore.server.domain.musical.service;

import encore.server.domain.musical.dto.request.MusicalCreateReq;
import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.dto.response.MusicalSeriesRes;
import encore.server.domain.musical.dto.response.MusicalSimpleRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.entity.MusicalActor;
import encore.server.domain.musical.entity.ShowTime;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.repository.ActorRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicalService {
    private final MusicalRepository musicalRepository;
    private final ActorRepository actorRepository;

    public List<MusicalRes> searchMusicalsByTitle(String keyword) {
        List<Musical> musicals = musicalRepository.findByTitleContaining(keyword);
        return musicals.stream()
                .map(MusicalRes::from) // dto로 수정
                .toList();
    }

    public MusicalDetailRes getMusicalDetail(Long musicalId) {
        //validation
        Musical musical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        return MusicalDetailRes.from(musical); //dto로 수정
    }

    public List<MusicalSeriesRes> getAllSeriesByMusicalId(Long musicalId) {
        Musical currentMusical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        List<Musical> musicals = musicalRepository.findByTitleAndDeletedAtIsNull(currentMusical.getTitle());

        return musicals.stream()
                .map(musical -> MusicalSeriesRes.builder()
                        .id(musical.getId())
                        .series(musical.getSeries())
                        .isCurrent(musical.getId().equals(musicalId)) // 현재 선택된 시리즈 표시
                        .build())
                .toList();
    }

    public List<MusicalSimpleRes> getFeaturedMusicals() {
        List<Musical> musicals = musicalRepository.findTop8ByIsFeaturedTrueOrderByStartDateAsc();
        return musicals.stream()
                .map(musical -> MusicalSimpleRes.builder()
                        .id(musical.getId())
                        .title(musical.getTitle())
                        .startDate(musical.getStartDate())
                        .endDate(musical.getEndDate())
                        .location(musical.getLocation())
                        .imageUrl(musical.getImageUrl())
                        .build())
                .toList();
    }

    public List<MusicalSimpleRes> getUpcomingMusicals() {
        LocalDateTime now = LocalDateTime.now();
        List<Musical> musicals = musicalRepository.findUpcomingMusicals(now);

        return musicals.stream()
                .limit(8)
                .map(musical -> MusicalSimpleRes.builder()
                        .id(musical.getId())
                        .title(musical.getTitle())
                        .startDate(musical.getStartDate())
                        .endDate(musical.getEndDate())
                        .location(musical.getLocation())
                        .imageUrl(musical.getImageUrl())
                        .build())
                .toList();
    }

    @Transactional
    public MusicalDetailRes addMusical(MusicalCreateReq request) {
        //validation: 중복 뮤지컬 확인
        if (musicalRepository.existsByTitleAndSeries(request.title(), request.series())) {
            throw new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);
        }

        // business logic
        // 뮤지컬 엔티티 생성
        Musical musical = Musical.builder()
                .title(request.title())
                .series(request.series())
                .location(request.location())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .runningTime(request.runningTime())
                .age(request.age())
                .imageUrl(request.imageUrl())
                .isFeatured(request.isFeatured())
                .build();



        // 배우 처리
        List<MusicalActor> musicalActors = request.actors().stream()
                .map(actorReq -> {
                    Actor actor = actorRepository.findById(actorReq.actorId())
                            .orElseThrow(() -> new ApplicationException(ErrorCode.ACTOR_NOT_FOUND_EXCEPTION));
                    return new MusicalActor(musical, actor, actorReq.roleName(), actorReq.isMainActor());
                }).collect(Collectors.toList());



        // 공연 시간 처리
        List<ShowTime> showTimes = request.showTimes().stream()
                .map(showTimeReq -> new ShowTime(musical, showTimeReq.day(), showTimeReq.time()))
                .collect(Collectors.toList());


        // 배우 추가 (리스트를 하나씩 추가)
        musicalActors.forEach(musical::addMusicalActors);
        // 공연 시간 추가 (리스트를 하나씩 추가)
        showTimes.forEach(musical::addShowTime);


        musicalRepository.save(musical);

        // return: 뮤지컬 상세 응답 반환
        return MusicalDetailRes.from(musical); //dto로 수정
    }

    @Transactional
    public MusicalDetailRes updateMusical(Long musicalId, MusicalCreateReq request) {
        // validation: 뮤지컬 존재 여부 확인
        Musical musical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        // business logic
        // 전달된 값만 업데이트
        if (request.title() != null) musical.updateTitle(request.title());
        if (request.series() != null) musical.updateSeries(request.series());
        if (request.location() != null) musical.updateLocation(request.location());
        if (request.startDate() != null) musical.updateStartDate(request.startDate());
        if (request.endDate() != null) musical.updateEndDate(request.endDate());
        if (request.runningTime() != null) musical.updateRunningTime(request.runningTime());
        if (request.age() != null) musical.updateAge(request.age());
        if (request.imageUrl() != null) musical.updateImageUrl(request.imageUrl());
        musical.updateIsFeatured(request.isFeatured());

        // 배우 정보 업데이트
        if (request.actors() != null) {
            musical.clearMusicalActors(); // 기존 배우 목록 삭제
            List<MusicalActor> updatedActors = request.actors().stream()
                    .map(actorReq -> {
                        Actor actor = actorRepository.findById(actorReq.actorId())
                                .orElseThrow(() -> new ApplicationException(ErrorCode.ACTOR_NOT_FOUND_EXCEPTION));
                        return new MusicalActor(musical, actor, actorReq.roleName(), actorReq.isMainActor());
                    }).collect(Collectors.toList());
            updatedActors.forEach(musical::addMusicalActors);
        }

        // 공연 시간 업데이트
        if (request.showTimes() != null) {
            musical.clearShowTimes(); // 기존 공연 시간 삭제
            List<ShowTime> updatedShowTimes = request.showTimes().stream()
                    .map(showTimeReq -> new ShowTime(musical, showTimeReq.day(), showTimeReq.time()))
                    .collect(Collectors.toList());
            updatedShowTimes.forEach(musical::addShowTime);
        }

        // return: 뮤지컬 상세 응답 반환
        return MusicalDetailRes.from(musical); //dto로 수정
    }

    @Transactional
    public void deleteMusical(Long musicalId) {
        // validation: 뮤지컬 존재 여부 확인
        Musical musical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        // business logic : 뮤지컬 soft delete
        musicalRepository.delete(musical);
    }

}
