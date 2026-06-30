package com.saucedemo.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static com.saucedemo.base.BaseTest.config;

public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected abstract String getPath();

    @Step("Navigate to page")
    public void navigate() {
        page.navigate(config.getBaseUrl() + getPath());
    }

    @Step("Fill field")
    protected void fill(Locator locator, String value) {
        locator.waitFor();
        locator.fill(value);
    }

    @Step("Click and wait for navigation")
    protected void clickAndWaitForNav(Locator locator) {
        locator.click();
        page.waitForLoadState();
    }

    @Step("Click element")
    protected void click(Locator locator) {
        locator.waitFor();
        locator.click();
    }

    public String getTitle()      { return page.title(); }
    public String getCurrentUrl() { return page.url(); }
}
