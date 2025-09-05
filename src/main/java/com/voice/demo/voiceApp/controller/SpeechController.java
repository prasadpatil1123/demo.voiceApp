package com.voice.demo.voiceApp.controller;

import com.voice.demo.voiceApp.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Autowired
    private SpeechService speechService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String path = speechService.convertSpeechToText(file);
            return ResponseEntity.ok("✅ Transcript saved at: " + path);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }
}
