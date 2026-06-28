package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for an individual product detail page.
 */
public class ProductPage extends BasePage {

    private final Locator productTitle;
    private final Locator productPrice;
    private final Locator addToCartButton;
    private final Locator cartCountBadge;

    public ProductPage(Page page) {
        super(page);
        productTitle    = page.locator(".product__title, h1.title, h1").first();
        productPrice    = page.locator(".price, .product__price").first();
        addToCartButton = page.locator("button[name='add'], [data-testid='add-to-cart']").first();
        cartCountBadge  = page.locator(".cart-count, .icon-cart__count, [data-cart-count]").first();
    }

    @Override
    protected String getPath() {
        return "/products";
    }

    public String getProductTitle() {
        productTitle.waitFor();
        return productTitle.textContent().trim();
    }

    public String getProductPrice() {
        productPrice.waitFor();
        return productPrice.textContent().trim();
    }

    @Step("Add product to cart")
    public ProductPage addToCart() {
        addToCartButton.waitFor();
        addToCartButton.click();
        // Wait for cart badge to update
        page.waitForLoadState();
        return this;
    }

    public boolean isAddToCartVisible() {
        return addToCartButton.isVisible();
    }
}
