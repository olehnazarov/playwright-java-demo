package com.saucedemo.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.ConfigReader;
import io.qameta.allure.Step;

/**
 * Base class for all Page Objects.
 * Provides reusable, Allure-annotated action helpers.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /** Each page declares its own relative path */
    protected abstract String getPath();

    @Step("Navigate to {path}")
    public void navigate() {
        page.navigate(ConfigReader.getBaseUrl() + getPath());
    }

    @Step("Fill '{locator}' with value")
    protected void fill(Locator locator, String value) {
        locator.waitFor();
        locator.fill(value);
    }

    @Step("Click element and wait for navigation")
    protected void clickAndWaitForNav(Locator locator) {
        locator.click();
        page.waitForLoadState();
    }

    @Step("Click element")
    protected void click(Locator locator) {
        locator.waitFor();
        locator.click();
    }

    public String getTitle() {
        return page.title();
    }

    public String getCurrentUrl() {
        return page.url();
    }
}
