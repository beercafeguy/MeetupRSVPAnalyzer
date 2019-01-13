package org.github.beercafeguy.distribution.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */

@SpringBootApplication
@ComponentScan(resourcePattern="org.github.beercafeguy.distribution.model")
public class MeetupApiApp {
	public static void main(String[] args) {
		SpringApplication.run(MeetupApiApp.class, args);               
	}
}
