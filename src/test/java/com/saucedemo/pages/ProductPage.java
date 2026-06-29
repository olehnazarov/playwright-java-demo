package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for /inventory-item.html — individual product detail page.
 */
public class ProductPage extends BasePage {

    private final Locator productName;
    private final Locator productDescription;
    private final Locator productPrice;
    private final Locator addToCartButton;
    private final Locator removeButton;
    private final Locator backButton;

    public ProductPage(Page page) {
        super(page);
        productName        = page.locator(".inventory_details_name");
        productDescription = page.locator(".inventory_details_desc");
        productPrice       = page.locator(".inventory_details_price");
        addToCartButton    = page.locator("button[data-test^='add-to-cart']");
        removeButton       = page.locator("button[data-test^='remove']");
        backButton         = page.locator("[data-test='back-to-products']");
    }

    @Override
    protected String getPath() {
        return "/inventory-item.html";
    }

    public String getProductName() {
        productName.waitFor();
        return productName.textContent().trim();
    }

    public String getProductDescription() {
        return productDescription.textContent().trim();
    }

    public String getProductPrice() {
        return productPrice.textContent().trim();
    }

    @Step("Add product to cart from detail page")
    public ProductPage addToCart() {
        addToCartButton.click();
        return this;
    }

    @Step("Remove product from cart on detail page")
    public ProductPage removeFromCart() {
        removeButton.click();
        return this;
    }

    public boolean isAddToCartVisible() {
        return addToCartButton.isVisible();
    }

    public boolean isRemoveButtonVisible() {
        return removeButton.isVisible();
    }

    @Step("Go back to products")
    public InventoryPage goBack() {
        clickAndWaitForNav(backButton);
        return new InventoryPage(page);
    }
}
