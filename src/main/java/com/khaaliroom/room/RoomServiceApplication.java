package com.khaaliroom.room;

import com.khaaliroom.room.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableMethodSecurity
public class RoomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				RoomServiceApplication.class,
				args
		);
	}
}
