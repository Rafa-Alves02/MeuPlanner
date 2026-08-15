package br.com.MeuPlanner.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final AppConfig INSTANCE = new AppConfig();

    private final Properties properties = new Properties();

    private AppConfig() {
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível carregar application.properties", e);
        }
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public String get(String key, String defaultValue) {
        String env = System.getenv(envKey(key));
        if (env != null && !env.isBlank()) return env;

        String prop = properties.getProperty(key);
        if (prop != null && !prop.isBlank()) return prop;

        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key, null);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String envKey(String propertyKey) {
        return propertyKey.toUpperCase().replace('.', '_');
    }
}