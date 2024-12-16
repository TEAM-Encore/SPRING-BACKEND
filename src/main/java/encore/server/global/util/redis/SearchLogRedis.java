package encore.server.global.util.redis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchLogRedis {

    private String name;

    @JsonCreator
    public SearchLogRedis(@JsonProperty("name") String name) {
        this.name = name;
    }
}
