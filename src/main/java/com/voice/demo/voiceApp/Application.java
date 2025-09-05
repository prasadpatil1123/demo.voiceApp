package com.voice.demo.voiceApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

// Open Postman →

//Method: POST

//URL: http://localhost:8080/api/speech/upload

//Body → form-data → key: file → select .mp3 file.   