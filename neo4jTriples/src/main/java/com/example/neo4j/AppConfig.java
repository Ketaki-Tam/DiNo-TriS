package com.example.neo4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class AppConfig {
    private static final String FILE_PATH = "path/to/syncMap.txt"; // Replace with your actual file path

    @Bean
    public Map<String, Integer> syncMap() throws IOException {
        Map<String, Integer> loadedMap = new HashMap<>();
        try (FileReader fr = new FileReader(FILE_PATH);
             BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0];
                int value = Integer.parseInt(parts[1]);
                loadedMap.put(key, value);
            }
        }
        return loadedMap;
    }

}

