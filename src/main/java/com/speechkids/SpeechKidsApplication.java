package com.speechkids;

import com.speechkids.config.AppProperties;
import com.speechkids.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AppProperties.class})
public class SpeechKidsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpeechKidsApplication.class, args);
    }
}
