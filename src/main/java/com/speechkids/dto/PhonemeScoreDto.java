package com.speechkids.dto;

public record PhonemeScoreDto(
        String phoneme,
        Integer accuracy
) {
}
