package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.AccountPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.TestDataFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Authentication")
@Feature("Login")
@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = new LoginPage(page());
        loginPage.navigate();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Valid credentials → redirect to account page")
    @Story("Successful login")
    void shouldLoginWithValidCredentials() {
        var user = TestDataFactory.standardUser();

        AccountPage accountPage = loginPage.loginAs(user.email(), user.password());

        Assertions.assertTrue(accountPage.isLoggedIn(),
                "Expected to be on account page after login");
        assertThat(page()).hasURL(org.junit.jupiter.api.Assertions::assertNotNull);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Invalid password → stays on login page")
    @Story("Failed login")
    void shouldStayOnLoginPageWithWrongPassword() {
        loginPage.loginExpectingError("user@example.com", "wrong_password");

        Assertions.assertTrue(loginPage.isOnLoginPage(),
                "Expected to remain on login page after failed login");
    }

    @ParameterizedTest(name = "Login attempt: {0}")
    @CsvSource({
            "Empty email,        , secret_sauce",
            "Empty password,     standard_user@saucedemo.com, ",
            "Wrong credentials,  bad@test.com, badpass"
    })
    @Severity(SeverityLevel.NORMAL)
    @Story("Input validation")
    void shouldHandleInvalidInputs(String scenario, String email, String password) {
        loginPage.loginExpectingError(
                email == null ? "" : email.trim(),
                password == null ? "" : password.trim()
        );

        Assertions.assertTrue(loginPage.isOnLoginPage(),
                "Scenario [" + scenario + "]: expected to stay on login page");
    }
}
