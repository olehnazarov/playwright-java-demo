package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for login page
 * Locators use stable IDs provided by the site.
 */
public class LoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        usernameInput = page.locator("#user-name");
        passwordInput = page.locator("#password");
        loginButton   = page.locator("#login-button");
        errorMessage  = page.locator("[data-test='error']");
    }

    @Override
    protected String getPath() {
        return "/";
    }

    @Step("Login as {username}")
    public InventoryPage loginAs(String username, String password) {
        fill(usernameInput, username);
        fill(passwordInput, password);
        clickAndWaitForNav(loginButton);
        return new InventoryPage(page);
    }

    @Step("Attempt login expecting error")
    public void loginExpectingError(String username, String password) {
        fill(usernameInput, username);
        fill(passwordInput, password);
        loginButton.click();
        errorMessage.waitFor();
    }

    public String getErrorMessage() {
        return errorMessage.textContent().trim();
    }

    public boolean isOnLoginPage() {
        return page.url().equals("https://www.saucedemo.com/")
                || page.url().endsWith("/index.html");
    }
}
