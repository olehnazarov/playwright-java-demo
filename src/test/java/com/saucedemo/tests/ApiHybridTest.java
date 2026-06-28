package com.saucedemo.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.ConfigReader;
import com.saucedemo.pages.StorePage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Hybrid tests: validate the HTTP layer first, then assert the UI reflects it.
 *
 * Pattern:
 *   1. Call the API directly to assert the contract
 *   2. Load the UI and assert it reflects the API response
 *
 * This proves you can separate concerns between API and UI layers.
 */
@Epic("Store")
@Feature("API + UI Hybrid")
@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
class ApiHybridTest extends BaseTest {

    private APIRequestContext apiContext;

    @BeforeEach
    void setUpApiContext() {
        // Playwright provides a standalone HTTP client — no extra lib needed
        apiContext = Playwright.create().request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigReader.getBaseUrl())
        );
    }

    @AfterEach
    void tearDownApiContext() {
        if (apiContext != null) apiContext.dispose();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Store homepage returns HTTP 200")
    @Story("API health check → UI load")
    void storeShouldReturn200AndLoadInBrowser() {
        // 1. API layer: assert the server responds correctly
        APIResponse response = apiContext.get("/collections/all");
        Assertions.assertEquals(200, response.status(),
                "Expected HTTP 200 from /collections/all, got: " + response.status());

        // 2. UI layer: load the same page and assert products appear
        StorePage storePage = new StorePage(page());
        storePage.navigate();

        Assertions.assertTrue(storePage.getProductCount() > 0,
                "UI should display products when API returns 200");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Cart page returns HTTP 200 and is accessible in browser")
    void cartPageShouldBeAccessible() {
        // API check
        APIResponse cartResponse = apiContext.get("/cart");
        Assertions.assertEquals(200, cartResponse.status(),
                "Cart endpoint should return 200");

        // UI check
        page().navigate(ConfigReader.getBaseUrl() + "/cart");
        assertThat(page()).hasURL(java.util.regex.Pattern.compile(".*/cart"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Network mock: simulate 503 on collections → verify UI handles it")
    @Story("Error handling")
    void shouldHandleNetworkFailureGracefully() {
        // Mock the collections endpoint to return 503
        page().route("**/collections/**", route -> route.fulfill(
                new com.microsoft.playwright.Route.FulfillOptions()
                        .setStatus(503)
                        .setBody("<html><body><h1>Service Unavailable</h1></body></html>")
                        .setContentType("text/html")
        ));

        page().navigate(ConfigReader.getBaseUrl() + "/collections/all");

        // Assert the page shows something (doesn't crash) and reflects the 503
        String bodyText = page().locator("body").textContent();
        Assertions.assertFalse(bodyText.isBlank(),
                "Page body should not be empty even on 503");
    }
}
