package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Page Object for the main store / product catalog page.
 */
public class StorePage extends BasePage {

    private final Locator productCards;
    private final Locator cartIcon;
    private final Locator searchInput;

    public StorePage(Page page) {
        super(page);
        productCards = page.locator(".product-item, .grid__item, [data-product-id]");
        cartIcon     = page.locator("a[href*='/cart'], .cart-icon, .icon-cart").first();
        searchInput  = page.locator("input[type='search'], #SearchInput").first();
    }

    @Override
    protected String getPath() {
        return "/collections/all";
    }

    @Step("Get all product titles")
    public List<String> getProductTitles() {
        productCards.first().waitFor();
        return productCards
                .locator(".product-item__title, .product-title, h3, h2")
                .allTextContents();
    }

    @Step("Get product count on page")
    public int getProductCount() {
        productCards.first().waitFor();
        return productCards.count();
    }

    @Step("Click on product at index {index}")
    public ProductPage clickProduct(int index) {
        productCards.nth(index)
                .locator("a").first()
                .click();
        page.waitForLoadState();
        return new ProductPage(page);
    }

    @Step("Open cart")
    public CartPage openCart() {
        clickAndWaitForNav(cartIcon);
        return new CartPage(page);
    }

    public boolean isLoaded() {
        return page.url().contains("/collections");
    }
}
