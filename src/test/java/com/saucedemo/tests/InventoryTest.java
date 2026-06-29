package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("Store")
@Feature("Product Catalog")
@ExtendWith(AllureJunit5.class)
class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void login() {
        var user = testData.standardUser();
        LoginPage loginPage = new LoginPage(page());
        loginPage.navigate();
        inventoryPage = loginPage.loginAs(user.username(), user.password());
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Inventory page shows 6 products")
    void shouldDisplaySixProducts() {
        Assertions.assertEquals(6, inventoryPage.getProductCount(),
                "Expected exactly 6 products on inventory page");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("All product names are non-empty")
    void allProductNamesShouldBeNonEmpty() {
        List<String> names = inventoryPage.getProductNames();
        names.forEach(name ->
                Assertions.assertFalse(name.isBlank(), "Found blank product name"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("All products have a price starting with $")
    void allProductsShouldHavePrice() {
        List<String> prices = inventoryPage.getProductPrices();
        prices.forEach(price ->
                Assertions.assertTrue(price.startsWith("$"),
                        "Price should start with $, got: " + price));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Adding product updates cart badge")
    void addingProductShouldUpdateCartBadge() {
        inventoryPage.addToCartByName("Sauce Labs Backpack");

        Assertions.assertEquals(1, inventoryPage.getCartBadgeCount(),
                "Cart badge should show 1 after adding a product");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Adding and removing product clears cart badge")
    void removingProductShouldClearCartBadge() {
        inventoryPage.addToCartByName("Sauce Labs Backpack");
        Assertions.assertEquals(1, inventoryPage.getCartBadgeCount());

        inventoryPage.removeFromCartByName("Sauce Labs Backpack");
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCount(),
                "Cart badge should be 0 after removing product");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clicking product opens detail page")
    void clickingProductOpensDetailPage() {
        ProductPage productPage = inventoryPage.clickProduct("Sauce Labs Backpack");

        Assertions.assertEquals("Sauce Labs Backpack", productPage.getProductName());
        Assertions.assertTrue(productPage.isAddToCartVisible());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Sort by price low to high works")
    void shouldSortByPriceLowToHigh() {
        inventoryPage.sortBy("lohi");
        List<String> prices = inventoryPage.getProductPrices();

        double first = parsePrice(prices.get(0));
        double last  = parsePrice(prices.get(prices.size() - 1));
        Assertions.assertTrue(first <= last,
                "First price should be <= last after sorting low to high");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Sort by name Z to A works")
    void shouldSortByNameZToA() {
        inventoryPage.sortBy("za");
        List<String> names = inventoryPage.getProductNames();

        Assertions.assertTrue(
                names.get(0).compareToIgnoreCase(names.get(names.size() - 1)) >= 0,
                "First name should be >= last after sorting Z to A"
        );
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("User can logout from inventory page")
    void shouldLogoutSuccessfully() {
        LoginPage loginPage = inventoryPage.logout();

        Assertions.assertTrue(loginPage.isOnLoginPage(),
                "Expected to be redirected to login page after logout");
    }

    private double parsePrice(String price) {
        return Double.parseDouble(price.replace("$", "").trim());
    }
}
