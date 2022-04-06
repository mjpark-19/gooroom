package com.angkeum.gooroom;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLOutput;

@SpringBootApplication
public class GooroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(GooroomApplication.class, args);
	}

}
