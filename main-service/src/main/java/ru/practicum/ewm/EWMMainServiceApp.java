package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.ewm")
public class EWMMainServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EWMMainServiceApp.class, args);
    }
}