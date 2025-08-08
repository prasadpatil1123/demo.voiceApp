package com.voice.demo.voiceApp.controller;

//import com.example.voiceapp.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/speech")
public class SpeechController {

    @Autowired
    private com.voice.demo.voiceApp.service.SpeechService speechService;

    @PostMapping("/upload")
    public String uploadAudio(@RequestParam("file") MultipartFile file) throws Exception {
        return speechService.convertSpeechToText(file);
    }
}
