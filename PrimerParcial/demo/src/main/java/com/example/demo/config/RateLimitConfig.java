package com.example.demo.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class RateLimitConfig {

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Bean
    public Bucket rateLimitBucket() {
        if (!rateLimitEnabled) {
            log.info("Rate limiting is disabled");
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(Long.MAX_VALUE, Refill.intervally(Long.MAX_VALUE, Duration.ofSeconds(1))))
                    .build();
        }

        log.info("Rate limiting enabled: {} requests per minute", requestsPerMinute);
        return Bucket.builder()
                .addLimit(Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))))
                .build();
    }
}