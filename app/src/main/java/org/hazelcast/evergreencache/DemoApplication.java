package org.hazelcast.evergreencache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication {

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

    @Bean
    public PersonRepository repository(JdbcTemplate template, CacheManager cacheManager) {
        return new PersonRepository(template,
                cacheManager.getCache("entities"),
                cacheManager.getCache("query"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}