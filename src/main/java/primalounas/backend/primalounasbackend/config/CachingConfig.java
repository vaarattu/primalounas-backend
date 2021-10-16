package primalounas.backend.primalounasbackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        CacheManager manager = new ConcurrentMapCacheManager("allWeeks", "currentWeek", "frequentCourses", "allCourses");
        return manager;
    }
}
