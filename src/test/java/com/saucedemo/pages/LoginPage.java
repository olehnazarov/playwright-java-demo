package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for the Sauce Demo login page.
 * URL: https://sauce-demo.myshopify.com/account/login
 */
public class LoginPage extends BasePage {

    // All locators declared as fields — never inlined in action methods
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        emailInput    = page.locator("#customer_email");
        passwordInput = page.locator("#customer_password");
        loginButton   = page.locator("[type='submit']").first();
        errorMessage  = page.locator(".errors, .notice, [data-testid='error']").first();
    }

    @Override
    protected String getPath() {
        return "/account/login";
    }

    /**
     * Logs in and returns the AccountPage (fluent chain).
     */
    @Step("Login as {email}")
    public AccountPage loginAs(String email, String password) {
        fill(emailInput, email);
        fill(passwordInput, password);
        clickAndWaitForNav(loginButton);
        return new AccountPage(page);
    }

    /**
     * Attempts login with bad credentials — stays on login page.
     */
    @Step("Attempt login with invalid credentials")
    public LoginPage loginExpectingError(String email, String password) {
        fill(emailInput, email);
        fill(passwordInput, password);
        loginButton.click();
        errorMessage.waitFor();
        return this;
    }

    public String getErrorMessage() {
        return errorMessage.textContent().trim();
    }

    public boolean isOnLoginPage() {
        return page.url().contains("/account/login");
    }
}
