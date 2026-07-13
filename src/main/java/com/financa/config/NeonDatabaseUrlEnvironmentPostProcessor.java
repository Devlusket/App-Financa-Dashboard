package com.financa.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Permite usar diretamente a connection string postgresql:// fornecida pelo Neon. */
public class NeonDatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String DATABASE_URL = "DATABASE_URL";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty(DATABASE_URL);
        if (databaseUrl == null || !(databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://"))) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", normalizarJdbcUrl(databaseUrl));

        if (uri.getUserInfo() != null) {
            String[] credentials = uri.getUserInfo().split(":", 2);
            properties.put("spring.datasource.username", decode(credentials[0]));
            if (credentials.length == 2) {
                properties.put("spring.datasource.password", decode(credentials[1]));
            }
        }
        environment.getPropertySources().addFirst(new MapPropertySource("neonDatabaseUrl", properties));
    }

    private String normalizarJdbcUrl(String databaseUrl) {
        String jdbcUrl = databaseUrl.startsWith("postgres://")
                ? "jdbc:postgresql://" + databaseUrl.substring("postgres://".length())
                : "jdbc:" + databaseUrl;
        return jdbcUrl.replace("channel_binding=", "channelBinding=");
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
