package com.example.lovekeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;

@SpringBootApplication
@EntityListeners(AuditingEntityListener.class)
public class LoveKeeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoveKeeperApplication.class, args);
	}

}
