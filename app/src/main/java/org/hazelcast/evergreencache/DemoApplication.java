package org.hazelcast.evergreencache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication {

    @Bean
    public PersonRepository repository(JdbcTemplate template) {
        return new PersonRepository(template);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}