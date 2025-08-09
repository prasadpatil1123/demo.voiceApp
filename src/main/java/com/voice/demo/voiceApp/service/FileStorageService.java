package com.voice.demo.voiceApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileStorageService {

    @Value("${transcript.folder}")
    private String transcriptFolder;

    public String saveTranscript(String transcript, String baseFileName) {
        try {
            Path folder = Path.of(transcriptFolder);
            Files.createDirectories(folder);

            String fileName = baseFileName + "_" + System.currentTimeMillis() + ".txt";
            Path filePath = folder.resolve(fileName);

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(transcript);
            }

            return "File saved at: " + filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save transcript: " + e.getMessage(), e);
        }
    }
}
