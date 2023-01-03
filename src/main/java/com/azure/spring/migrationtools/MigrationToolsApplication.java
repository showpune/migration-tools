package com.azure.spring.migrationtools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;

@SpringBootApplication
@Slf4j
public class MigrationToolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MigrationToolsApplication.class, args);
	}

}
