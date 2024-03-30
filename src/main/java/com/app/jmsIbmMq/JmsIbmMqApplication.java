package com.app.jmsIbmMq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class JmsIbmMqApplication {
	public static void main(String[] args) {
		SpringApplication.run(JmsIbmMqApplication.class, args);
	}
}
