package com.techblog.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // ✅ Ye import add karo
import org.springframework.context.annotation.ComponentScan; // ✅ Ye import add karo
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // ✅ Ye import add karo
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = "com.techblog") // ✅ Add karo (taaki saare service/controller mil jayein)
@EnableJpaRepositories(basePackages = "com.techblog.backend.repository") // ✅ Ye add karo (Repository dhundhne ke liye)
@EntityScan(basePackages = "com.techblog.backend.entity") // ✅ Ye add karo (Entity dhundhne ke liye)
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}