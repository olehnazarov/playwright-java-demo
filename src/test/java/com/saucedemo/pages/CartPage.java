package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for the shopping cart page.
 */
public class CartPage extends BasePage {

    private final Locator cartItems;
    private final Locator checkoutButton;
    private final Locator emptyCartMessage;

    public CartPage(Page page) {
        super(page);
        cartItems        = page.locator(".cart__item, [data-cart-item], tr.cart__row").first();
        checkoutButton   = page.locator("[name='checkout'], [data-testid='checkout'], .cart__checkout").first();
        emptyCartMessage = page.locator(".cart--empty, .empty-cart").first();
    }

    @Override
    protected String getPath() {
        return "/cart";
    }

    public boolean isCartEmpty() {
        return emptyCartMessage.isVisible();
    }

    public boolean hasCheckoutButton() {
        return checkoutButton.isVisible();
    }

    @Step("Proceed to checkout")
    public void proceedToCheckout() {
        checkoutButton.waitFor();
        clickAndWaitForNav(checkoutButton);
    }

    public boolean isLoaded() {
        return page.url().contains("/cart");
    }
}
