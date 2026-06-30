package com.saucedemo.base;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.saucedemo.config.TestConfig;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.stream.Collectors;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

/**
 * Base class for all API tests.
 * Provides a configured APIRequestContext, shared request/response
 * logging helpers, and Allure attachment utilities.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public abstract class BaseApiTest {

    @Autowired
    protected TestConfig config;

    private Playwright playwright;
    protected APIRequestContext apiContext;

    /**
     * Each subclass provides the base URL for its API context.
     */
    protected abstract String getBaseUrl();

    @BeforeEach
    void setUpApiContext() {
        playwright = Playwright.create();
        apiContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(getBaseUrl())
        );
        log.info("API context initialized for base URL: {}", getBaseUrl());
    }

    @AfterEach
    void tearDownApiContext() {
        if (apiContext != null) apiContext.dispose();
        if (playwright != null) playwright.close();
        log.info("API context disposed");
    }

    // ---------------------------------------------------------------
    // HTTP methods — each wraps the raw call with Allure step,
    // SLF4J log, and request/response attachments.
    // ---------------------------------------------------------------

    protected APIResponse get(String path) {
        return Allure.step("GET " + path, () -> {
            RequestOptions options = RequestOptions.create()
                    .setHeader("Accept", "application/json");

            logAndAttachRequest("GET", getBaseUrl() + path, Map.of("Accept", "application/json"));
            APIResponse response = apiContext.get(path, options);
            logAndAttachResponse("GET", path, response);
            return response;
        });
    }

    protected APIResponse post(String path, String jsonBody) {
        return Allure.step("POST " + path, () -> {
            RequestOptions options = RequestOptions.create()
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-Type", "application/json")
                    .setData(jsonBody);

            logAndAttachRequest("POST", getBaseUrl() + path,
                    Map.of("Accept", "application/json", "Content-Type", "application/json"),
                    jsonBody);
            APIResponse response = apiContext.post(path, options);
            logAndAttachResponse("POST", path, response);
            return response;
        });
    }

    // ---------------------------------------------------------------
    // Assertion helpers
    // ---------------------------------------------------------------

    protected void assertStatus(APIResponse response, int expected) {
        Allure.step("Assert HTTP status is " + expected, () -> {
            log.info("Expected status: {}, actual: {}", expected, response.status());
            org.junit.jupiter.api.Assertions.assertEquals(expected, response.status(),
                    "Expected HTTP " + expected + ", got: " + response.status());
        });
    }

    // ---------------------------------------------------------------
    // Logging & Allure attachment helpers
    // ---------------------------------------------------------------

    private void logAndAttachRequest(String method, String url, Map<String, String> headers) {
        logAndAttachRequest(method, url, headers, null);
    }

    private void logAndAttachRequest(String method, String url,
                                     Map<String, String> headers, String body) {
        String details = "Method: %s\nURL: %s\nHeaders:\n%s%s".formatted(
                method, url, formatHeaders(headers),
                body != null ? "\nBody:\n" + body : "");

        log.info("Request:\n{}", details);
        Allure.addAttachment(
                "Request — " + method + " " + url,
                "text/plain",
                new ByteArrayInputStream(details.getBytes()),
                ".txt"
        );
    }

    private void logAndAttachResponse(String method, String path, APIResponse response) {
        String body = safeBody(response);
        String details = "Status: %d\nHeaders:\n%s\nBody:\n%s".formatted(
                response.status(),
                formatHeaders(response.headers()),
                body);

        log.info("Response {} {} → {}", method, path, response.status());
        Allure.addAttachment(
                "Response — " + method + " " + path + " [" + response.status() + "]",
                "text/plain",
                new ByteArrayInputStream(details.getBytes()),
                ".txt"
        );
    }

    private String formatHeaders(Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(e -> "  " + e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String safeBody(APIResponse response) {
        try {
            String text = response.text();
            return text.length() > 3000 ? text.substring(0, 3000) + "...[truncated]" : text;
        } catch (Exception e) {
            log.warn("Could not read response body: {}", e.getMessage());
            return "[unreadable body: " + e.getMessage() + "]";
        }
    }
}
