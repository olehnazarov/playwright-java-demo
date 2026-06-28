package com.saucedemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("test.properties")) {
            if (is == null) throw new RuntimeException("test.properties not found on classpath");
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test.properties", e);
        }
    }

    public static String getBaseUrl() {
        return System.getProperty("base.url", props.getProperty("base.url"));
    }

    public static String getBrowser() {
        return System.getProperty("browser", props.getProperty("browser", "chromium"));
    }

    public static boolean isHeadless() {
        String val = System.getProperty("headless", props.getProperty("headless", "true"));
        return Boolean.parseBoolean(val);
    }

    public static int getDefaultTimeout() {
        String val = System.getProperty("default.timeout.ms",
                props.getProperty("default.timeout.ms", "10000"));
        return Integer.parseInt(val);
    }
}
