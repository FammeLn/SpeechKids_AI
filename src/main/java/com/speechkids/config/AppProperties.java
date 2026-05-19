package com.speechkids.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    @NotBlank
    private String audioStorageDir;

    public String getAudioStorageDir() {
        return audioStorageDir;
    }

    public void setAudioStorageDir(String audioStorageDir) {
        this.audioStorageDir = audioStorageDir;
    }
}
