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
		SpringApplication.run(CentralizedHealthCardApplication.class, args);
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
