package encore.server.global.config;

import encore.server.global.util.redis.SearchLogRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(clazz));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(clazz));
        return template;
    }

    // 리뷰 조회수용 RedisTemplate
    @Bean
    public RedisTemplate<String, Object> reviewViewedRedisTemplate() {
        return createRedisTemplate(Object.class);
    }

    // 최근검색어용 RedisTemplate
    @Bean
    public RedisTemplate<String, SearchLogRedis> searchLogRedisTemplate() {
        return createRedisTemplate(SearchLogRedis.class);
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory());
        builder.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1))); // 1시간 TTL
        return builder.build();
    }
}