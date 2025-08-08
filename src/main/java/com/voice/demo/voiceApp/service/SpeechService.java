package com.voice.demo.voiceApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SpeechService {

    @Value("${vosk.model.path}")
    private String voskModelPath;

    @Value("${transcript.folder}")
    private String transcriptFolder;

    private Model model;

    @jakarta.annotation.PostConstruct
    public void init() throws IOException {
        model = new Model(voskModelPath);
        Files.createDirectories(Paths.get(transcriptFolder));
    }

    public String convertSpeechToText(MultipartFile audioFile) throws Exception {

        // Step 1: Save uploaded file temporarily
        Path tempAudio = Files.createTempFile("upload_", "_" + audioFile.getOriginalFilename());
        Files.write(tempAudio, audioFile.getBytes());

        // Step 2: Convert to WAV using FFmpeg
        Path wavFile = Files.createTempFile("converted_", ".wav");
        Process ffmpeg = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", tempAudio.toString(),
                "-ar", "16000",
                "-ac", "1",
                "-f", "wav",
                wavFile.toString()
        ).redirectErrorStream(true).start();
        ffmpeg.waitFor();

        // Step 3: Run Vosk recognition
        StringBuilder transcript = new StringBuilder();
        try (InputStream ais = new BufferedInputStream(new FileInputStream(wavFile.toFile()));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = ais.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String jsonResult = recognizer.getResult();
                    String text = new JSONObject(jsonResult).optString("text");
                    if (!text.isEmpty()) {
                        transcript.append(text).append("\n"); // New line after each segment
                    }
                }
            }

            String finalJson = recognizer.getFinalResult();
            String finalText = new JSONObject(finalJson).optString("text");
            if (!finalText.isEmpty()) {
                transcript.append(finalText).append("\n");
            }
        }

        // Step 4: Save transcript to file
        String transcriptFileName = "transcript_" + System.currentTimeMillis() + ".txt";
        Path transcriptPath = Paths.get(transcriptFolder, transcriptFileName);
        Files.write(transcriptPath, transcript.toString().getBytes());

        // Step 5: Cleanup temp files
        Files.deleteIfExists(tempAudio);
        Files.deleteIfExists(wavFile);

        return transcriptPath.toString();
    }
}
