package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.ProductPage;
import com.saucedemo.pages.StorePage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Store")
@Feature("Product Catalog")
@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
class StoreTest extends BaseTest {

    private StorePage storePage;

    @BeforeEach
    void openStorePage() {
        storePage = new StorePage(page());
        storePage.navigate();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Products are visible on catalog page")
    void shouldDisplayProducts() {
        int count = storePage.getProductCount();

        Assertions.assertTrue(count > 0,
                "Expected at least 1 product, got: " + count);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Product titles are non-empty strings")
    void productTitlesShouldBeNonEmpty() {
        List<String> titles = storePage.getProductTitles();

        Assertions.assertFalse(titles.isEmpty(), "Expected at least one product title");
        titles.forEach(title ->
                Assertions.assertFalse(title.isBlank(),
                        "Found a blank product title"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Clicking a product opens its detail page")
    void shouldNavigateToProductPage() {
        ProductPage productPage = storePage.clickProduct(0);

        Assertions.assertFalse(productPage.getProductTitle().isBlank(),
                "Product detail page should show a title");
        Assertions.assertTrue(productPage.isAddToCartVisible(),
                "Add to Cart button should be visible on product page");
    }

    @ParameterizedTest(name = "Product at index {0} has a price")
    @ValueSource(ints = {0, 1, 2})
    @Severity(SeverityLevel.NORMAL)
    void eachProductShouldHaveAPrice(int index) {
        // Navigate fresh each time (parallel-safe — each test has its own Page)
        int total = storePage.getProductCount();
        Assumptions.assumeTrue(index < total, "Not enough products for index " + index);

        ProductPage product = storePage.clickProduct(index);

        Assertions.assertFalse(product.getProductPrice().isBlank(),
                "Product at index " + index + " should have a price");
    }
}
