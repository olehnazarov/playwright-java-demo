package com.saucedemo.tests.ui;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
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
@Feature("Shopping Cart")
@ExtendWith(AllureJunit5.class)
class CartTest extends BaseTest {

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
    @DisplayName("Cart is empty on fresh login")
    void cartShouldBeEmptyOnFreshLogin() {
        CartPage cartPage = inventoryPage.openCart();

        Assertions.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after fresh login");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Added product appears in cart")
    void addedProductShouldAppearInCart() {
        inventoryPage.addToCartByName("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.openCart();

        List<String> names = cartPage.getItemNames();
        Assertions.assertTrue(names.contains("Sauce Labs Backpack"),
                "Cart should contain the added product");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Multiple products can be added to cart")
    void shouldAddMultipleProducts() {
        inventoryPage.addToCartByName("Sauce Labs Backpack");
        inventoryPage.addToCartByName("Sauce Labs Bike Light");
        CartPage cartPage = inventoryPage.openCart();

        Assertions.assertEquals(2, cartPage.getCartItemCount(),
                "Cart should contain 2 items");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Product can be removed from cart")
    void shouldRemoveProductFromCart() {
        inventoryPage.addToCartByName("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.openCart();
        cartPage.removeItem("Sauce Labs Backpack");

        Assertions.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after removing the only item");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Continue shopping returns to inventory")
    void shouldReturnToInventoryFromCart() {
        Assertions.assertTrue(
                inventoryPage.openCart().continueShopping().isLoaded(),
                "Should return to inventory page after clicking Continue Shopping");
    }
}
