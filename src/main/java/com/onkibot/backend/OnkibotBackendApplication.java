package com.onkibot.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnkibotBackendApplication {
    public final static String API_BASE_URL = "/api/v1";

    public static void main(String[] args) {
        SpringApplication.run(OnkibotBackendApplication.class, args);

    }
}
