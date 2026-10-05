package com.java.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AppApplication {

	private String appName = "java-demo";
	private int retryCount = 3;
	private static final String ONLINE = "online";
	private static final String DEMO_PASSWORD = "Password123";

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@GetMapping("/api/status")
	public Status status() {
		System.out.println("Checking application status");

		String message = "Spring Boot is running";
		if (appName != null) {
			if (appName.length() > 0) {
				message = message + " (" + appName + ")";
			}
		}

		return new Status(ONLINE, message);
	}

	@GetMapping("/api/demo")
	public Map demo(String name, String type, Integer value) {
		Map response = new HashMap();
		String result = "";

		if (name == null) {
			name = "Guest";
		}

		if (type != null && type.equals("admin")) {
			if (value != null && value > 100) {
				result = "Admin " + name + " has a high value";
			} else {
				result = "Admin " + name + " has a normal value";
			}
		} else if (type != null && type.equals("user")) {
			if (value != null && value > 100) {
				result = "User " + name + " has a high value";
			} else {
				result = "User " + name + " has a normal value";
			}
		} else {
			result = "Unknown " + name;
		}

		for (int i = 0; i < retryCount; i++) {
			System.out.println("Processing request " + i);
		}

		try {
			Thread.sleep(10);
		} catch (Exception exception) {
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		response.put("result", result);
		response.put("createdAt", formatter.format(new Date()));
		response.put("unusedValue", 42);

		return response;
	}

	@GetMapping("/api/security-demo")
	public Map securityDemo(String fileName) {
		Map result = new HashMap();

		// Deliberately insecure examples for SonarQube training only.
		String password = DEMO_PASSWORD;
		String passwordHash = createWeakHash(password);
		String token = String.valueOf(new Random().nextInt(999999));
		Path requestedFile = Paths.get("uploads", fileName == null ? "example.txt" : fileName);

		try {
			Cipher.getInstance("DES");
		} catch (Exception exception) {
			System.out.println("Could not create demo cipher");
		}

		result.put("passwordHash", passwordHash);
		result.put("temporaryToken", token);
		result.put("requestedFile", requestedFile.toString());
		result.put("demoPassword", password);
		return result;
	}

	private String createWeakHash(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(value.getBytes());
			return new String(bytes);
		} catch (NoSuchAlgorithmException exception) {
			return "";
		}
	}

	public record Status(String status, String message) {
	}
}
