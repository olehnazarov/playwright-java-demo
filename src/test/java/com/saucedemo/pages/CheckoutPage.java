package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.base.BasePage;
import io.qameta.allure.Step;

/**
 * Page Object for /checkout-step-one.html and /checkout-step-two.html.
 */
public class CheckoutPage extends BasePage {

    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator postalCodeInput;
    private final Locator continueButton;
    private final Locator finishButton;
    private final Locator confirmationHeader;
    private final Locator errorMessage;
    private final Locator summaryTotal;

    public CheckoutPage(Page page) {
        super(page);
        firstNameInput     = page.locator("[data-test='firstName']");
        lastNameInput      = page.locator("[data-test='lastName']");
        postalCodeInput    = page.locator("[data-test='postalCode']");
        continueButton     = page.locator("[data-test='continue']");
        finishButton       = page.locator("[data-test='finish']");
        confirmationHeader = page.locator(".complete-header");
        errorMessage       = page.locator("[data-test='error']");
        summaryTotal       = page.locator(".summary_total_label");
    }

    @Override
    protected String getPath() {
        return "/checkout-step-one.html";
    }

    @Step("Fill checkout info: {firstName} {lastName}, {postalCode}")
    public CheckoutPage fillInfo(String firstName, String lastName, String postalCode) {
        fill(firstNameInput, firstName);
        fill(lastNameInput, lastName);
        fill(postalCodeInput, postalCode);
        return this;
    }

    @Step("Continue to order summary")
    public CheckoutPage continueToSummary() {
        clickAndWaitForNav(continueButton);
        return this;
    }

    @Step("Finish order")
    public CheckoutPage finishOrder() {
        clickAndWaitForNav(finishButton);
        return this;
    }

    public boolean isOrderConfirmed() {
        confirmationHeader.waitFor();
        return confirmationHeader.textContent().contains("Thank you");
    }

    public String getConfirmationMessage() {
        return confirmationHeader.textContent().trim();
    }

    public String getSummaryTotal() {
        return summaryTotal.textContent().trim();
    }

    public String getErrorMessage() {
        return errorMessage.textContent().trim();
    }

    public boolean hasError() {
        return errorMessage.isVisible();
    }
}
