package com.voice.demo.voiceApp.service;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SpeechService {

    @Value("${vosk.model.en}")
    private String voskModelEn;

    @Value("${vosk.model.hi}")
    private String voskModelHi;

    @Value("${transcript.folder}")
    private String transcriptFolder;

    private Model modelEn;
    private Model modelHi;

    @PostConstruct
    public void init() throws IOException {
        modelEn = new Model(voskModelEn);
        modelHi = new Model(voskModelHi);
        Files.createDirectories(Paths.get(transcriptFolder));
    }

    public String convertSpeechToText(MultipartFile audioFile) throws Exception {
        // Save uploaded file temporarily
        Path tempAudio = Files.createTempFile("upload_", "_" + audioFile.getOriginalFilename());
        Files.write(tempAudio, audioFile.getBytes());

        // Convert to WAV (mono, 16kHz)
        Path wavFile = Files.createTempFile("converted_", ".wav");
        Process ffmpeg = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", tempAudio.toString(),
                "-acodec", "pcm_s16le",
                "-ar", "16000",
                "-ac", "1",
                wavFile.toString()
        ).redirectErrorStream(true).start();
        ffmpeg.waitFor();

        StringBuilder transcript = new StringBuilder();

        try (InputStream ais = new BufferedInputStream(new FileInputStream(wavFile.toFile()));
             Recognizer recEn = new Recognizer(modelEn, 16000);
             Recognizer recHi = new Recognizer(modelHi, 16000)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = ais.read(buffer)) >= 0) {
                boolean enResult = recEn.acceptWaveForm(buffer, bytesRead);
                boolean hiResult = recHi.acceptWaveForm(buffer, bytesRead);

                if (enResult) {
                    String en = new JSONObject(recEn.getResult()).optString("text");
                    if (!en.isEmpty()) {
                        transcript.append("Agent 1 (EN): ").append(en).append("\n");
                    }
                }

                if (hiResult) {
                    String hi = new JSONObject(recHi.getResult()).optString("text");
                    if (!hi.isEmpty()) {
                        transcript.append("Agent 2 (HI): ").append(hi).append("\n");
                    }
                }
            }

            // final results
            String finalEn = new JSONObject(recEn.getFinalResult()).optString("text");
            String finalHi = new JSONObject(recHi.getFinalResult()).optString("text");

            if (!finalEn.isEmpty()) transcript.append("Agent 1 (EN): ").append(finalEn).append("\n");
            if (!finalHi.isEmpty()) transcript.append("Agent 2 (HI): ").append(finalHi).append("\n");
        }

        // Save transcript
        String transcriptFileName = "transcript_" + System.currentTimeMillis() + ".txt";
        Path transcriptPath = Paths.get(transcriptFolder, transcriptFileName);
        Files.write(transcriptPath, transcript.toString().trim().getBytes());

        return transcriptPath.toString();
    }
}
