package encore.server.domain.hashtag.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/hashtag")
@Tag(name = "Hashtag", description = "해시태그 API")
public class HashtagController {
}
