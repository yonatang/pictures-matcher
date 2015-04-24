package idc.storyalbum.matcher.conf;

import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by yonatan on 23/4/2015.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    public KeyGenerator eventScoreKeyGen() {
        return (target, method, params) -> {
            StoryEvent event = (StoryEvent) params[1];
            double nonFuzziness = (double) params[2];
            return new StringBuilder()
                    .append(event.getId())
                    .append("||")
                    .append(nonFuzziness)
                    .toString();
        };
    }

    @Bean
    public KeyGenerator imageFitScoreKeyGen() {
        return (target, method, params) -> {
            AnnotatedImage image = (AnnotatedImage) params[0];
            StoryEvent event = (StoryEvent) params[1];
            return new StringBuilder()
                    .append(event.getId())
                    .append("||")
                    .append(image.getImageFilename())
                    .toString();
        };
    }
}
