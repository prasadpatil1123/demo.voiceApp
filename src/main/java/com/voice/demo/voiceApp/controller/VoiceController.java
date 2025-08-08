//package com.voice.demo.voiceApp.controller;
//
//import com.voice.demo.voiceApp.service.SpeechService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/voice")
//@RequiredArgsConstructor
//public class VoiceController {
//
//    private final SpeechService speechService;
//
//    @PostMapping("/transcribe")
//    public ResponseEntity<String> transcribe(@RequestParam("file") MultipartFile file) {
//        try {
//            String savedPath = speechService.processAudioAndSaveTranscript(file);
//            return ResponseEntity.ok(savedPath); // only file path
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//}
