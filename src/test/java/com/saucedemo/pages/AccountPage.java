package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;

/**
 * Page Object for the logged-in account page.
 */
public class AccountPage extends BasePage {

    private final Locator welcomeHeading;
    private final Locator orderHistorySection;
    private final Locator logoutLink;

    public AccountPage(Page page) {
        super(page);
        welcomeHeading      = page.locator("h1, .account__title").first();
        orderHistorySection = page.locator(".account, #mainContent").first();
        logoutLink          = page.locator("a[href*='logout']").first();
    }

    @Override
    protected String getPath() {
        return "/account";
    }

    public boolean isLoggedIn() {
        return page.url().contains("/account") && !page.url().contains("/login");
    }

    public String getWelcomeHeading() {
        welcomeHeading.waitFor();
        return welcomeHeading.textContent().trim();
    }

    public boolean hasOrderHistorySection() {
        return orderHistorySection.isVisible();
    }

    public LoginPage logout() {
        logoutLink.waitFor();
        clickAndWaitForNav(logoutLink);
        return new LoginPage(page);
    }
}
