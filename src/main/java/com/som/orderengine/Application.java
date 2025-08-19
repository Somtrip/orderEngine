package com.som.orderengine;

import com.som.orderengine.observers.AlertObserver;
import com.som.orderengine.observers.LoggerObserver;
import com.som.orderengine.processing.EventProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private final EventProcessor processor;

    public Application(EventProcessor processor) {
        this.processor = processor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        
        processor.addObserver(new LoggerObserver());
        processor.addObserver(new AlertObserver());
    }
}
