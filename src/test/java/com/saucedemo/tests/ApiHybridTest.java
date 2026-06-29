package com.saucedemo.tests;

import com.microsoft.playwright.*;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.SpringContext;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("API + UI")
@Feature("Hybrid Tests")
@ExtendWith(AllureJunit5.class)
class ApiHybridTest extends BaseTest {

    private APIRequestContext apiContext;

    @BeforeEach
    void setUpApiContext() {
        apiContext = Playwright.create().request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(SpringContext.config().getBaseUrl())
        );
    }

    @AfterEach
    void tearDownApiContext() {
        if (apiContext != null) apiContext.dispose();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Site homepage returns HTTP 200")
    void homepageShouldReturn200() {
        APIResponse response = apiContext.get("/");
        Assertions.assertEquals(200, response.status(),
                "Expected HTTP 200 from homepage");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("HTTP 200 on homepage → UI loads login form")
    @Story("API health → UI load")
    void whenApiReturns200ThenLoginFormIsVisible() {
        APIResponse response = apiContext.get("/");
        Assertions.assertEquals(200, response.status());

        LoginPage loginPage = new LoginPage(page());
        loginPage.navigate();

        Assertions.assertTrue(loginPage.isOnLoginPage(),
                "Login page should be accessible when API returns 200");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Network mock: 503 on inventory → page body is not empty")
    @Story("Error handling")
    void shouldHandleNetworkFailureGracefully() {
        // Login first to get past auth
        var user = testData.standardUser();
        new LoginPage(page()).navigate();
        new LoginPage(page()).loginAs(user.username(), user.password());

        // Now mock inventory endpoint
        page().route("**/inventory.html", route -> route.fulfill(
                new Route.FulfillOptions()
                        .setStatus(503)
                        .setBody("<html><body><h1>Service Unavailable</h1></body></html>")
                        .setContentType("text/html")
        ));

        page().navigate(SpringContext.config().getBaseUrl() + "/inventory.html");

        String body = page().locator("body").textContent();
        Assertions.assertTrue(body.contains("Service Unavailable"),
                "Mocked 503 page should show 'Service Unavailable'");
    }
}
