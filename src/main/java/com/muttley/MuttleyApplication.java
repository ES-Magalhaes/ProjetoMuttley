package com.muttley;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MuttleyApplication {

    public static void main(String[] args) {
        // Este comando inicia todo o ecossistema do Spring Boot
        SpringApplication.run(MuttleyApplication.class, args);
    }
}