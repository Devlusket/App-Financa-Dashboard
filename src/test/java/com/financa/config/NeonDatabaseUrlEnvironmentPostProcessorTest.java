package com.financa.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeonDatabaseUrlEnvironmentPostProcessorTest {

    @Test
    void converteConnectionStringDoNeonParaJdbcEExtraiCredenciais() {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "DATABASE_URL", "postgresql://usuario:senha@host.neon.tech/neondb?sslmode=require&channel_binding=require"
        )));

        new NeonDatabaseUrlEnvironmentPostProcessor().postProcessEnvironment(environment, new SpringApplication());

        assertEquals(
                "jdbc:postgresql://usuario:senha@host.neon.tech/neondb?sslmode=require&channelBinding=require",
                environment.getProperty("spring.datasource.url")
        );
        assertEquals("usuario", environment.getProperty("spring.datasource.username"));
        assertEquals("senha", environment.getProperty("spring.datasource.password"));
    }
}
