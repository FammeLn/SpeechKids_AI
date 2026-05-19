package com.speechkids.dto;

public record SystemHealthDto(
        String onlineModelName,
        String offlineModelName,
        String aiServiceStatus,
        Long averageProcessingTimeMs,
        boolean gpuAvailable
) {
}
