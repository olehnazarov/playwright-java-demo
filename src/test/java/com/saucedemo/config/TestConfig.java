package com.saucedemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("com.saucedemo")
@PropertySource("classpath:test.properties")
public class TestConfig {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${browser:chromium}")
    private String browser;

    @Value("${headless:true}")
    private boolean headless;

    @Value("${default.timeout.ms:10000}")
    private int defaultTimeoutMs;

    public String getBaseUrl()       { return System.getProperty("base.url", baseUrl); }
    public String getBrowser()       { return System.getProperty("browser", browser); }
    public boolean isHeadless()      { return Boolean.parseBoolean(System.getProperty("headless", String.valueOf(headless))); }
    public int getDefaultTimeout()   { return defaultTimeoutMs; }

    /**
     * Required for @Value placeholder resolution.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
