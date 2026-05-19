package com.speechkids.controller;

import com.speechkids.dto.AnalyzeResultDto;
import com.speechkids.enums.SessionMode;
import com.speechkids.service.AudioService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalyzeResultDto analyze(@RequestParam("audio") MultipartFile audio,
                                    @RequestParam("childId") UUID childId,
                                    @RequestParam("sessionId") UUID sessionId,
                                    @RequestParam("exerciseItemId") UUID exerciseItemId,
                                    @RequestParam("targetWord") String targetWord,
                                    @RequestParam("mode") SessionMode mode) {
        return audioService.analyze(audio, childId, sessionId, exerciseItemId, targetWord, mode);
    }
}
