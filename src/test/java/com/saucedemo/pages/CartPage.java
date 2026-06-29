package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Page Object for /cart.html — shopping cart.
 */
public class CartPage extends BasePage {

    private final Locator cartItems;
    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator checkoutButton;
    private final Locator continueShoppingButton;

    public CartPage(Page page) {
        super(page);
        cartItems              = page.locator(".cart_item");
        itemNames              = page.locator(".inventory_item_name");
        itemPrices             = page.locator(".inventory_item_price");
        checkoutButton         = page.locator("[data-test='checkout']");
        continueShoppingButton = page.locator("[data-test='continue-shopping']");
    }

    @Override
    protected String getPath() {
        return "/cart.html";
    }

    public boolean isLoaded() {
        return page.url().contains("cart.html");
    }

    public int getCartItemCount() {
        return cartItems.count();
    }

    public List<String> getItemNames() {
        return itemNames.allTextContents();
    }

    public List<String> getItemPrices() {
        return itemPrices.allTextContents();
    }

    public boolean isCartEmpty() {
        return cartItems.count() == 0;
    }

    @Step("Remove item from cart: {name}")
    public void removeItem(String name) {
        page.locator(".cart_item")
                .filter(new Locator.FilterOptions().setHasText(name))
                .locator("button")
                .click();
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        clickAndWaitForNav(checkoutButton);
        return new CheckoutPage(page);
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        clickAndWaitForNav(continueShoppingButton);
        return new InventoryPage(page);
    }
}
