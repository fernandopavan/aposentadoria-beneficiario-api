package com.aposentadoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableJpaAuditing
@SpringBootApplication
@EnableWebSocket
@EnableWebSocketMessageBroker
public class BeneficiarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeneficiarioApplication.class, args);
    }

}
