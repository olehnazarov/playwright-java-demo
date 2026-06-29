package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Epic("Authentication")
@Feature("Login")
@ExtendWith(AllureJunit5.class)
class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = new LoginPage(page());
        loginPage.navigate();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Standard user can login successfully")
    @Story("Successful login")
    void shouldLoginWithValidCredentials() {
        var user = testData.standardUser();
        InventoryPage inventoryPage = loginPage.loginAs(user.username(), user.password());

        Assertions.assertTrue(inventoryPage.isLoaded(),
                "Expected to land on inventory page after login");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Locked out user sees error message")
    @Story("Locked account")
    void shouldShowErrorForLockedUser() {
        var user = testData.lockedUser();
        loginPage.loginExpectingError(user.username(), user.password());

        String error = loginPage.getErrorMessage();
        Assertions.assertTrue(error.contains("locked out"),
                "Expected 'locked out' error, got: " + error);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Wrong password shows error")
    @Story("Failed login")
    void shouldShowErrorForWrongPassword() {
        loginPage.loginExpectingError("standard_user", "wrong_password");

        String error = loginPage.getErrorMessage();
        Assertions.assertFalse(error.isBlank(), "Expected error message to be shown");
        Assertions.assertTrue(loginPage.isOnLoginPage(), "Expected to stay on login page");
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Empty username, '', secret_sauce",
            "Empty password, standard_user, ''",
            "Both empty, '', ''"
    })
    @Severity(SeverityLevel.NORMAL)
    @Story("Input validation")
    void shouldValidateEmptyFields(String scenario, String username, String password) {
        loginPage.loginExpectingError(username, password);

        Assertions.assertFalse(loginPage.getErrorMessage().isBlank(),
                "Scenario [" + scenario + "]: expected validation error");
    }
}
