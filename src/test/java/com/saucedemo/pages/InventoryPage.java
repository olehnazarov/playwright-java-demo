package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Page Object for /inventory.html — main product catalog after login.
 */
public class InventoryPage extends BasePage {

    private final Locator productItems;
    private final Locator productNames;
    private final Locator productPrices;
    private final Locator cartBadge;
    private final Locator cartLink;
    private final Locator sortDropdown;
    private final Locator burgerMenu;
    private final Locator logoutLink;

    public InventoryPage(Page page) {
        super(page);
        productItems  = page.locator(".inventory_item");
        productNames  = page.locator(".inventory_item_name");
        productPrices = page.locator(".inventory_item_price");
        cartBadge     = page.locator(".shopping_cart_badge");
        cartLink      = page.locator(".shopping_cart_link");
        sortDropdown  = page.locator("[data-test='product-sort-container']");
        burgerMenu    = page.locator("#react-burger-menu-btn");
        logoutLink    = page.locator("#logout_sidebar_link");
    }

    @Override
    protected String getPath() {
        return "/inventory.html";
    }

    public boolean isLoaded() {
        return page.url().contains("inventory.html");
    }

    public int getProductCount() {
        productItems.first().waitFor();
        return productItems.count();
    }

    public List<String> getProductNames() {
        productNames.first().waitFor();
        return productNames.allTextContents();
    }

    public List<String> getProductPrices() {
        productPrices.first().waitFor();
        return productPrices.allTextContents();
    }

    @Step("Add product to cart by name: {name}")
    public void addToCartByName(String name) {
        page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHasText(name))
                .locator("button")
                .click();
    }

    @Step("Remove product from cart by name: {name}")
    public void removeFromCartByName(String name) {
        page.locator(".inventory_item")
                .filter(new Locator.FilterOptions().setHasText(name))
                .locator("button")
                .click();
    }

    public int getCartBadgeCount() {
        if (!cartBadge.isVisible()) return 0;
        return Integer.parseInt(cartBadge.textContent().trim());
    }

    @Step("Open cart")
    public CartPage openCart() {
        cartLink.click();
        page.waitForLoadState();
        return new CartPage(page);
    }

    @Step("Sort products by: {option}")
    public void sortBy(String option) {
        sortDropdown.selectOption(option);
    }

    @Step("Click on product: {name}")
    public ProductPage clickProduct(String name) {
        productNames.filter(new Locator.FilterOptions().setHasText(name)).click();
        page.waitForLoadState();
        return new ProductPage(page);
    }

    @Step("Logout")
    public LoginPage logout() {
        burgerMenu.click();
        logoutLink.waitFor();
        clickAndWaitForNav(logoutLink);
        return new LoginPage(page);
    }
}
