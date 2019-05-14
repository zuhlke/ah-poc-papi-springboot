package com.aimlesshammer.pocpapispringboot;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PocPapiSpringbootApplication {
    private static ConfigurableApplicationContext context;

    public static void start(int portNumber) {
        SpringServerProperties springServerProperties = new SpringServerProperties().withPort(portNumber);
        context = new SpringApplicationBuilder(PocPapiSpringbootApplication.class)
                .properties(springServerProperties.asMap())
                .run();
    }

    public static void stop() {
        int exitCode = 0;
        SpringApplication.exit(context, (ExitCodeGenerator) () -> exitCode);
    }
}
