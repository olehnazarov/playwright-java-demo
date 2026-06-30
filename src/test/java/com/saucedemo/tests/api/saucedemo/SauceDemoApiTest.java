package com.saucedemo.tests.api.saucedemo;

import com.saucedemo.base.BaseApiTest;
import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("SauceDemo API")
@Feature("Hybrid — HTTP + UI")
@ExtendWith(AllureJunit5.class)
class SauceDemoApiTest extends BaseApiTest {

    @Override
    protected String getBaseUrl() {
        return config.getBaseUrl();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Homepage returns HTTP 200")
    void homepageShouldReturn200() {
        assertStatus(get("/"), 200);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Cart page returns HTTP 200")
    void cartPageShouldReturn200() {
        assertStatus(get("/cart.html"), 200);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Inventory page returns HTTP 200")
    void inventoryPageShouldReturn200() {
        assertStatus(get("/inventory.html"), 200);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent page returns 404")
    void nonExistentPageShouldReturn404() {
        assertStatus(get("/this-page-does-not-exist"), 404);
    }
}
