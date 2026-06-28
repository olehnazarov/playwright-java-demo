package com.saucedemo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads test data from src/test/resources/testdata/users.json.
 * Add more factories here as the suite grows.
 */
public class TestDataFactory {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode usersNode;

    static {
        try (InputStream is = TestDataFactory.class
                .getClassLoader()
                .getResourceAsStream("testdata/users.json")) {
            if (is == null) throw new RuntimeException("testdata/users.json not found");
            usersNode = mapper.readTree(is).get("users");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users.json", e);
        }
    }

    public record UserCredentials(String email, String password, String role) {}

    public static UserCredentials getUserByRole(String role) {
        for (JsonNode user : usersNode) {
            if (user.get("role").asText().equals(role)) {
                return new UserCredentials(
                        user.get("email").asText(),
                        user.get("password").asText(),
                        user.get("role").asText()
                );
            }
        }
        throw new RuntimeException("No user found with role: " + role);
    }

    public static UserCredentials standardUser() {
        return getUserByRole("standard");
    }

    public static UserCredentials lockedUser() {
        return getUserByRole("locked");
    }

    public static UserCredentials problemUser() {
        return getUserByRole("problem");
    }
}
