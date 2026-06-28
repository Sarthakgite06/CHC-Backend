package com.onkar.chc;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CentralizedHealthCardApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(CentralizedHealthCardApplication.class, args);
	}

	private static void loadEnv() {
		String[] paths = {".env", "chc/.env", "../.env"};
		boolean loadedValidKey = false;
		for (String pathStr : paths) {
			java.nio.file.Path envPath = java.nio.file.Paths.get(pathStr);
			if (java.nio.file.Files.exists(envPath)) {
				try {
					boolean[] hasValidKeyInFile = {false};
					java.nio.file.Files.lines(envPath).forEach(line -> {
						String trimmed = line.trim();
						if (!trimmed.isEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
							int eqIdx = trimmed.indexOf('=');
							String key = trimmed.substring(0, eqIdx).trim();
							String val = trimmed.substring(eqIdx + 1).trim();
							if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
								val = val.substring(1, val.length() - 1);
							} else if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
								val = val.substring(1, val.length() - 1);
							}
							if (!val.isEmpty() && !val.equals("YOUR_GEMINI_API_KEY_HERE")) {
								System.setProperty(key, val);
								if ("GEMINI_API_KEY".equals(key)) {
									hasValidKeyInFile[0] = true;
								}
							}
						}
					});
					System.out.println("✅ Processed environment variables from " + envPath.toAbsolutePath());
					if (hasValidKeyInFile[0]) {
						loadedValidKey = true;
					}
				} catch (Exception e) {
					System.err.println("❌ Failed to load env from " + pathStr + ": " + e.getMessage());
				}
			}
			if (loadedValidKey) {
				break;
			}
		}
	}

	/**
	 * Seeds the pre-defined admin account on application startup.
	 * Admin email: admin07@chc.com | password: adminCHC07
	 * Admin has no signup — only this seeded account exists.
	 */
	@Bean
	CommandLineRunner seedAdmin(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			// Only seed if admin doesn't already exist
			if (userRepo.findByUserName("admin07").isEmpty()) {
				UserEntity admin = UserEntity.builder()
						.userName("admin07")
						.firstName("CHC")
						.lastName("Team")
						.password(passwordEncoder.encode("adminCHC07"))
						.email("admin07@chc.com")
						.contactNo(0L)
						.role("Admin")
						.createdAt("2026-01-01")
						.build();

				userRepo.save(admin);
				System.out.println("✅ Admin account seeded: admin07@chc.com / adminCHC07");
			} else {
				System.out.println("ℹ️ Admin account already exists, skipping seed.");
			}
		};
	}
}
