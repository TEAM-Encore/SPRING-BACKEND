package encore.server.domain.ticket.service;

import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorSeedService {

    private final ActorRepository actorRepository;

    @Transactional
    public Result upsertFromClasspathCsv() {
        int inserted = 0, updated = 0, skipped = 0;

        try {
            ClassPathResource resource = new ClassPathResource("actors.csv");
            if (!resource.exists()) {
                log.warn("[ActorSeed] actors.csv 가 classpath 에 없습니다.");
                return new Result(inserted, updated, skipped);
            }

            try (Reader in = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                Iterable<CSVRecord> records = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(in);

                for (CSVRecord r : records) {
                    String name = r.get("name").trim();
                    String birthYearStr = r.isMapped("birthYear") ? r.get("birthYear").trim() : "";
                    String imageUrlStr = r.isMapped("actorImageUrl") ? r.get("actorImageUrl").trim() : "";

                    if (name.isEmpty()) { skipped++; continue; }

                    Integer birthYear = null;
                    if (!birthYearStr.isEmpty()) {
                        try { birthYear = Integer.parseInt(birthYearStr); }
                        catch (NumberFormatException e) {
                            log.warn("[ActorSeed] 잘못된 birthYear: '{}'", birthYearStr);
                        }
                    }
                    String actorImageUrl = imageUrlStr.isEmpty() ? null : imageUrlStr;

                    var existing = actorRepository.findByNameAndBirthYearNullable(name, birthYear);

                    if (existing.isPresent()) {
                        Actor a = existing.get();
                        boolean needUpdate = false;

                        if ((a.getActorImageUrl() == null && actorImageUrl != null) ||
                                (a.getActorImageUrl() != null && !a.getActorImageUrl().equals(actorImageUrl))) {
                            a = Actor.builder()
                                    .id(a.getId())
                                    .name(a.getName())
                                    .birthYear(a.getBirthYear())
                                    .actorImageUrl(actorImageUrl)
                                    .build();
                            needUpdate = true;
                        }

                        if (needUpdate) {
                            actorRepository.save(a);
                            updated++;
                        } else {
                            skipped++;
                        }
                    } else {
                        actorRepository.save(Actor.builder()
                                .name(name)
                                .birthYear(birthYear)
                                .actorImageUrl(actorImageUrl)
                                .build());
                        inserted++;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("actors.csv 업서트 중 오류", e);
        }
        return new Result(inserted, updated, skipped);
    }

    public record Result(int inserted, int updated, int skipped) {}
}
