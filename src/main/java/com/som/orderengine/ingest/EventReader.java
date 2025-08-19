package com.som.orderengine.ingest;


import com.som.orderengine.events.BaseEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class EventReader {
    private final EventParser parser;

    public EventReader(EventParser parser) {
        this.parser = parser;
    }

    public Stream<String> stream(String file) {
        try {
            Path p = Path.of(file);
            if (!p.toFile().exists()) {
                System.err.println("File not found: " + p.toAbsolutePath());
                return Stream.empty();
            }
            return Files.lines(p);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return Stream.empty();
        }
    }

    public Optional<BaseEvent> parse(String line) {
        return parser.parseLine(line);
    }
}

