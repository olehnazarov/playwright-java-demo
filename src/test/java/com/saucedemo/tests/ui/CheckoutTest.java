package com.saucedemo.tests.ui;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("Store")
@Feature("Checkout")
@ExtendWith(AllureJunit5.class)
class CheckoutTest extends BaseTest {

    private CartPage cartPage;

    @BeforeEach
    void loginAndAddProduct() {
        var user = testData.standardUser();
        LoginPage loginPage = new LoginPage(page());
        loginPage.navigate();
        InventoryPage inventoryPage = loginPage.loginAs(user.username(), user.password());
        inventoryPage.addToCartByName("Sauce Labs Backpack");
        cartPage = inventoryPage.openCart();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Complete checkout flow ends with confirmation")
    @Story("Happy path checkout")
    void shouldCompleteCheckoutSuccessfully() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout()
                .fillInfo("John", "Doe", "12345")
                .continueToSummary()
                .finishOrder();

        Assertions.assertTrue(checkoutPage.isOrderConfirmed(),
                "Expected order confirmation message");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Checkout shows order total")
    void checkoutSummaryShouldShowTotal() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout()
                .fillInfo("John", "Doe", "12345")
                .continueToSummary();

        String total = checkoutPage.getSummaryTotal();
        Assertions.assertFalse(total.isBlank(), "Order total should not be blank");
        Assertions.assertTrue(total.contains("Total"), "Summary should contain 'Total'");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Checkout shows error when first name is missing")
    @Story("Checkout validation")
    void shouldShowErrorWhenFirstNameMissing() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout()
                .fillInfo("", "Doe", "12345")
                .continueToSummary();

        Assertions.assertTrue(checkoutPage.hasError(),
                "Expected validation error when first name is missing");
        Assertions.assertTrue(checkoutPage.getErrorMessage().contains("First Name"),
                "Error should mention First Name");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Checkout shows error when postal code is missing")
    @Story("Checkout validation")
    void shouldShowErrorWhenPostalCodeMissing() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout()
                .fillInfo("John", "Doe", "")
                .continueToSummary();

        Assertions.assertTrue(checkoutPage.hasError(),
                "Expected validation error when postal code is missing");
    }
}
